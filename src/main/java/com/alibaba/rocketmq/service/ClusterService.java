package com.alibaba.rocketmq.service;

import com.alibaba.rocketmq.common.Table;
import com.alibaba.rocketmq.common.protocol.body.ClusterInfo;
import com.alibaba.rocketmq.common.protocol.body.KVTable;
import com.alibaba.rocketmq.common.protocol.route.BrokerData;
import com.alibaba.rocketmq.tools.admin.DefaultMQAdminExt;
import com.alibaba.rocketmq.tools.command.cluster.ClusterListSubCommand;
import com.alibaba.rocketmq.validate.CmdTrace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import static com.alibaba.rocketmq.common.Tool.str;


/**
 * 集群服务类
 *
 * @author yankai913@gmail.com
 * @date 2014-2-8
 */
@Service
public class ClusterService extends AbstractService {

    static final Logger logger = LoggerFactory.getLogger(ClusterService.class);


    @CmdTrace(cmdClazz = ClusterListSubCommand.class)
    public Table list() throws Throwable {
        Throwable t = null;
        DefaultMQAdminExt defaultMQAdminExt = getDefaultMQAdminExt();
        try {
            defaultMQAdminExt.start();
            Table table = doList(defaultMQAdminExt);
            return table;
        } catch (Throwable e) {
            logger.error(e.getMessage(), e);
            t = e;
        } finally {
            shutdownDefaultMQAdminExt(defaultMQAdminExt);
        }
        throw t;
    }


    private Table doList(DefaultMQAdminExt defaultMQAdminExt) throws Exception {

        ClusterInfo clusterInfoSerializeWrapper = defaultMQAdminExt.examineBrokerClusterInfo();

        String[] instanceThead =
                new String[]{"Role", "Addr", "Version", "InTPS", "OutTPS", "InTotalYest",
                        "OutTotalYest", "InTotalToday", "OutTotalToday"};

        Set<Map.Entry<String, Set<String>>> clusterSet =
                clusterInfoSerializeWrapper.getClusterAddrTable().entrySet();

        int clusterRow = clusterSet.size();

        // 第一层
        Table clusterTable = new Table(new String[]{"Cluster Name", "Broker Detail"}, clusterRow);

        Iterator<Map.Entry<String, Set<String>>> itCluster = clusterSet.iterator();

        while (itCluster.hasNext()) {
            Map.Entry<String, Set<String>> next = itCluster.next();

            // 集群名称
            String clusterName = next.getKey();

            Set<String> brokerNameSet = new HashSet<String>();
            brokerNameSet.addAll(next.getValue());

            Object[] clusterTR = clusterTable.createTR();

            // 第一列
            clusterTR[0] = clusterName;


            Table brokerTable =
                    new Table(new String[]{"Broker Name", "Broker Instance"}, brokerNameSet.size());

            // 第二列
            clusterTR[1] = brokerTable;
            clusterTable.insertTR(clusterTR);// A // 第一层

            for (String brokerName : brokerNameSet) {

                Object[] brokerTR = brokerTable.createTR();

                brokerTR[0] = brokerName;

                BrokerData brokerData = clusterInfoSerializeWrapper.getBrokerAddrTable().get(brokerName);
                if (brokerData != null) {
                    Set<Map.Entry<Long, String>> brokerAddrSet = brokerData.getBrokerAddrs().entrySet();

                    // broker 地址
                    Iterator<Map.Entry<Long, String>> itAddr = brokerAddrSet.iterator();

                    Table instanceTable = new Table(instanceThead, brokerAddrSet.size());
                    brokerTR[1] = instanceTable;
                    brokerTable.insertTR(brokerTR);// B // 第二层

                    // broker 地址
                    while (itAddr.hasNext()) {
                        Object[] instanceTR = instanceTable.createTR();

                        // broker 地址
                        Map.Entry<Long, String> next1 = itAddr.next();
                        double in = 0;
                        double out = 0;
                        String version = "";

                        long InTotalYest = 0;
                        long OutTotalYest = 0;
                        long InTotalToday = 0;
                        long OutTotalToday = 0;

                        try {
                            KVTable kvTable = defaultMQAdminExt.fetchBrokerRuntimeStats(next1.getValue());
                            String putTps = kvTable.getTable().get("putTps");
                            String getTransferedTps = kvTable.getTable().get("getTransferedTps");
                            version = kvTable.getTable().get("brokerVersionDesc");
                            {
                                String[] tpss = putTps.split(" ");
                                if (tpss != null && tpss.length > 0) {
                                    in = Double.parseDouble(tpss[0]);
                                }
                            }

                            {
                                String[] tpss = getTransferedTps.split(" ");
                                if (tpss != null && tpss.length > 0) {
                                    out = Double.parseDouble(tpss[0]);
                                }
                            }

                            if(next1.getKey().longValue()==0){
                                instanceTR[0] ="master";
                            }
                            else if(next1.getKey().longValue()==1){
                                instanceTR[0] ="slave";
                            }
                            else{
                                instanceTR[0] ="ERROR";
                            }


                            String aa = "<a  target='_blank' href='/rocketmq-console/broker/brokerStats.do?brokerAddr=" + next1.getValue() + "'><label data-toggle='tooltip' data-placement='right' " +
                                    "title='点击查看broker 状态！'>" + next1.getValue() + "</label></a>";
                            instanceTR[1] = aa;

                            instanceTR[2] = version;
                            instanceTR[3] = str(in);
                            instanceTR[4] = str(out);

                            String msgPutTotalYesterdayMorning =
                                    kvTable.getTable().get("msgPutTotalYesterdayMorning");
                            String msgPutTotalTodayMorning =
                                    kvTable.getTable().get("msgPutTotalTodayMorning");
                            String msgPutTotalTodayNow = kvTable.getTable().get("msgPutTotalTodayNow");
                            String msgGetTotalYesterdayMorning =
                                    kvTable.getTable().get("msgGetTotalYesterdayMorning");
                            String msgGetTotalTodayMorning =
                                    kvTable.getTable().get("msgGetTotalTodayMorning");
                            String msgGetTotalTodayNow = kvTable.getTable().get("msgGetTotalTodayNow");

                            InTotalYest =
                                    Long.parseLong(msgPutTotalTodayMorning)
                                            - Long.parseLong(msgPutTotalYesterdayMorning);
                            OutTotalYest =
                                    Long.parseLong(msgGetTotalTodayMorning)
                                            - Long.parseLong(msgGetTotalYesterdayMorning);

                            InTotalToday =
                                    Long.parseLong(msgPutTotalTodayNow)
                                            - Long.parseLong(msgPutTotalTodayMorning);
                            OutTotalToday =
                                    Long.parseLong(msgGetTotalTodayNow)
                                            - Long.parseLong(msgGetTotalTodayMorning);

                            instanceTR[5] = str(InTotalYest);
                            instanceTR[6] = str(OutTotalYest);
                            instanceTR[7] = str(InTotalToday);
                            instanceTR[8] = str(OutTotalToday);
                            instanceTable.insertTR(instanceTR);// C // 第三层
                        } catch (Exception e) {
                            logger.error(e.getMessage(), e);
                        }

                    }
                }
            }
        }
        return clusterTable;
    }

}
