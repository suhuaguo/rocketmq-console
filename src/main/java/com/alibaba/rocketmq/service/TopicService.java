package com.alibaba.rocketmq.service;

import com.alibaba.rocketmq.common.MixAll;
import com.alibaba.rocketmq.common.Table;
import com.alibaba.rocketmq.common.TopicConfig;
import com.alibaba.rocketmq.common.UtilAll;
import com.alibaba.rocketmq.common.admin.ConsumeStats;
import com.alibaba.rocketmq.common.admin.OffsetWrapper;
import com.alibaba.rocketmq.common.admin.TopicOffset;
import com.alibaba.rocketmq.common.admin.TopicStatsTable;
import com.alibaba.rocketmq.common.message.MessageQueue;
import com.alibaba.rocketmq.common.protocol.body.GroupList;
import com.alibaba.rocketmq.common.protocol.body.TopicList;
import com.alibaba.rocketmq.common.protocol.route.TopicRouteData;
import com.alibaba.rocketmq.config.ConfigureInitializer;
import com.alibaba.rocketmq.tools.admin.DefaultMQAdminExt;
import com.alibaba.rocketmq.tools.command.CommandUtil;
import com.alibaba.rocketmq.tools.command.topic.*;
import com.alibaba.rocketmq.validate.CmdTrace;
import org.apache.commons.cli.Option;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.alibaba.rocketmq.common.Tool.str;

/**
 * @author yankai913@gmail.com
 * @date 2014-2-11
 */
@Service
public class TopicService extends AbstractService {

    static final Logger logger = LoggerFactory.getLogger(TopicService.class);

    @Autowired
    ConfigureInitializer configureInitializer;


    @CmdTrace(cmdClazz = TopicListSubCommand.class)
    public Table list() throws Throwable {
        Throwable t = null;
        DefaultMQAdminExt defaultMQAdminExt = getDefaultMQAdminExt();
        try {
            defaultMQAdminExt.start();
            TopicList topicList = defaultMQAdminExt.fetchAllTopicList();
            int row = topicList.getTopicList().size();
            if (row > 0) {
                Table table = new Table(new String[]{"topic"}, row);
                for (String topicName : topicList.getTopicList()) {

                    if (topicName.startsWith(MixAll.RETRY_GROUP_TOPIC_PREFIX) ||
                        topicName.startsWith(MixAll.DLQ_GROUP_TOPIC_PREFIX) ||
                        topicName.startsWith(MixAll.BENCHMARK_TOPIC) ||
                        topicName.startsWith(MixAll.DEFAULT_TOPIC) ||
                        topicName.startsWith(MixAll.OFFSET_MOVED_EVENT) ||
                        topicName.startsWith(MixAll.SELF_TEST_TOPIC)) {

                        table.addExtData(topicName, topicName);

                    } else {
                        Object[] tr = table.createTR();
                        tr[0] = topicName;
                        table.insertTR(tr);
                    }
                }

                return table;
            } else {
                throw new IllegalStateException("defaultMQAdminExt.fetchAllTopicList() is blank");
            }
        } catch (Throwable e) {
            logger.error(e.getMessage(), e);
            t = e;
        } finally {
            shutdownDefaultMQAdminExt(defaultMQAdminExt);
        }
        throw t;
    }


    @CmdTrace(cmdClazz = TopicStatusSubCommand.class)
    public Table stats(String topicName) throws Throwable {
        Throwable t = null;
        DefaultMQAdminExt defaultMQAdminExt = getDefaultMQAdminExt();
        try {
            defaultMQAdminExt.start();
            TopicStatsTable topicStatsTable = defaultMQAdminExt.examineTopicStats(topicName);

            List<MessageQueue> mqList = new LinkedList<MessageQueue>();
            mqList.addAll(topicStatsTable.getOffsetTable().keySet());
            Collections.sort(mqList);

            // System.out.printf("%-32s  %-4s  %-20s  %-20s    %s\n",//
            // "#Broker Name",//
            // "#QID",//
            // "#Min Offset",//
            // "#Max Offset",//
            // "#Last Updated" //
            // );
            String[] thead =
                    new String[]{"#Broker Name", "#QID", "#Min Offset", "#Max Offset", "#Last Updated"};
            Table table = new Table(thead, mqList.size());
            for (MessageQueue mq : mqList) {
                TopicOffset topicOffset = topicStatsTable.getOffsetTable().get(mq);

                String humanTimestamp = "";
                if (topicOffset.getLastUpdateTimestamp() > 0) {
                    humanTimestamp = UtilAll.timeMillisToHumanString2(topicOffset.getLastUpdateTimestamp());
                }

                Object[] tr = table.createTR();
                tr[0] = UtilAll.frontStringAtLeast(mq.getBrokerName(), 32);
                tr[1] = str(mq.getQueueId());
                tr[2] = str(topicOffset.getMinOffset());
                tr[3] = str(topicOffset.getMaxOffset());
                tr[4] = humanTimestamp;

                table.insertTR(tr);
                // System.out.printf("%-32s  %-4d  %-20d  %-20d    %s\n",//
                // UtilAll.frontStringAtLeast(mq.getBrokerName(), 32),//
                // mq.getQueueId(),//
                // topicOffset.getMinOffset(),//
                // topicOffset.getMaxOffset(),//
                // humanTimestamp //
                // );
            }
            return table;
        } catch (Throwable e) {
            logger.error(e.getMessage(), e);
            t = e;
        } finally {
            shutdownDefaultMQAdminExt(defaultMQAdminExt);
        }
        throw t;
    }

    final static UpdateTopicSubCommand updateTopicSubCommand = new UpdateTopicSubCommand();


    public Collection<Option> getOptionsForUpdate() {
        return getOptions(updateTopicSubCommand);
    }


    @CmdTrace(cmdClazz = UpdateTopicSubCommand.class)
    public boolean update(String topic, String readQueueNums, String writeQueueNums, String perm,
                          String brokerAddr, String clusterName) throws Throwable {
        Throwable t = null;
        DefaultMQAdminExt defaultMQAdminExt = getDefaultMQAdminExt();

        try {
            TopicConfig topicConfig = new TopicConfig();
            topicConfig.setReadQueueNums(8);
            topicConfig.setWriteQueueNums(8);
            topicConfig.setTopicName(topic);

            if (StringUtils.isNotBlank(readQueueNums)) {
                topicConfig.setReadQueueNums(Integer.parseInt(readQueueNums));
            }

            if (StringUtils.isNotBlank(writeQueueNums)) {
                topicConfig.setWriteQueueNums(Integer.parseInt(writeQueueNums));
            }

            if (StringUtils.isNotBlank(perm)) {
                topicConfig.setPerm(translatePerm(perm));
            }

            if (StringUtils.isNotBlank(brokerAddr)) {
                defaultMQAdminExt.start();
                defaultMQAdminExt.createAndUpdateTopicConfig(brokerAddr, topicConfig);
                return true;
            } else if (StringUtils.isNotBlank(clusterName)) {

                defaultMQAdminExt.start();

                Set<String> masterSet =
                        CommandUtil.fetchMasterAddrByClusterName(defaultMQAdminExt, clusterName);
                for (String addr : masterSet) {
                    defaultMQAdminExt.createAndUpdateTopicConfig(addr, topicConfig);
                }
                return true;
            } else {
                throw new IllegalStateException("clusterName or brokerAddr can not be all blank");
            }
        } catch (Throwable e) {
            logger.error(e.getMessage(), e);
            t = e;
        } finally {
            shutdownDefaultMQAdminExt(defaultMQAdminExt);
        }
        throw t;
    }

    final static DeleteTopicSubCommand deleteTopicSubCommand = new DeleteTopicSubCommand();


    public Collection<Option> getOptionsForDelete() {
        return getOptions(deleteTopicSubCommand);
    }


    @CmdTrace(cmdClazz = DeleteTopicSubCommand.class)
    public boolean delete(String topicName, String clusterName) throws Throwable {
        Throwable t = null;
        DefaultMQAdminExt adminExt = getDefaultMQAdminExt();
        try {
            if (StringUtils.isNotBlank(clusterName)) {
                adminExt.start();
                Set<String> masterSet = CommandUtil.fetchMasterAddrByClusterName(adminExt, clusterName);
                adminExt.deleteTopicInBroker(masterSet, topicName);
                Set<String> nameServerSet = null;
                if (StringUtils.isNotBlank(configureInitializer.getNamesrvAddr())) {
                    String[] ns = configureInitializer.getNamesrvAddr().split(";");
                    nameServerSet = new HashSet<String>(Arrays.asList(ns));
                }
                adminExt.deleteTopicInNameServer(nameServerSet, topicName);
                return true;
            } else {
                throw new IllegalStateException("clusterName is blank");
            }
        } catch (Throwable e) {
            logger.error(e.getMessage(), e);
            t = e;
        } finally {
            shutdownDefaultMQAdminExt(adminExt);
        }
        throw t;
    }


    @CmdTrace(cmdClazz = TopicRouteSubCommand.class)
    public TopicRouteData route(String topicName) throws Throwable {
        Throwable t = null;
        DefaultMQAdminExt adminExt = getDefaultMQAdminExt();
        try {
            adminExt.start();
            TopicRouteData topicRouteData = adminExt.examineTopicRouteInfo(topicName);
            return topicRouteData;
        } catch (Throwable e) {
            logger.error(e.getMessage(), e);
            t = e;
        } finally {
            shutdownDefaultMQAdminExt(adminExt);
        }
        throw t;
    }


    /**
     * 抓取所有的 Topic
     *
     * @return
     */
    public List<String> fetchAllTopic() {
        List<String> topics = new ArrayList<String>();

        DefaultMQAdminExt defaultMQAdminExt = getDefaultMQAdminExt();

        System.out.println("namesrv---" + configureInitializer.getNamesrvAddr());

        defaultMQAdminExt.setNamesrvAddr(configureInitializer.getNamesrvAddr());
        try {
            defaultMQAdminExt.start();
            TopicList topicList = defaultMQAdminExt.fetchAllTopicList();
            if (null != topicList) {
                for (String topic : topicList.getTopicList()) {

                    if (topic.startsWith(MixAll.RETRY_GROUP_TOPIC_PREFIX) ||
                            topic.startsWith(MixAll.DLQ_GROUP_TOPIC_PREFIX) ||
                            topic.contains(MixAll.SELF_TEST_TOPIC) ||
                            topic.contains(MixAll.BENCHMARK_TOPIC) ||
                            topic.contains(MixAll.OFFSET_MOVED_EVENT) ||
                            topic.contains(MixAll.DEFAULT_TOPIC)
                            )
                        continue;

                    // 上面除外
                    topics.add(topic);

                }
            }

        } catch (Exception e) {
            logger.error(e.getMessage());
        } finally {
            shutdownDefaultMQAdminExt(defaultMQAdminExt);
        }

        return topics;
    }

    /**
     * 抓取所有的 Topic@group
     *
     * @return
     */
    public List<String> fetchGroupByTopic() {
        List<String> topics = new ArrayList<String>();
        List<String> topicGroups = new ArrayList<String>();

        DefaultMQAdminExt defaultMQAdminExt = getDefaultMQAdminExt();
        defaultMQAdminExt.setNamesrvAddr(configureInitializer.getNamesrvAddr());

        try {
            defaultMQAdminExt.start();
            TopicList topicList = defaultMQAdminExt.fetchAllTopicList();
            if (null != topicList) {
                for (String topic : topicList.getTopicList()) {
                    if (!topic.startsWith(MixAll.RETRY_GROUP_TOPIC_PREFIX)) { // 把 有前缀的 Topic 去除掉
                        topics.add(topic);
                    }
                }
            }
            for (String topic : topics) {

                GroupList groupList = defaultMQAdminExt.queryTopicConsumeByWho(topic);
                HashSet<String> groups = groupList.getGroupList();
                for (String group : groups) {
                    topicGroups.add(topic + "@" + group);
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            shutdownDefaultMQAdminExt(defaultMQAdminExt);
        }

        return topicGroups;
    }


    /**
     * 抓取 通过 Group 获取 Toipc
     *
     * @return
     */
    public Object[] getTopicByConsumer(String consumerGroup) {
        Set<String> topics = new HashSet<String>();


        DefaultMQAdminExt defaultMQAdminExt = getDefaultMQAdminExt();
        try {
            defaultMQAdminExt.start();
            ConsumeStats consumeStats = defaultMQAdminExt.examineConsumeStats(consumerGroup);
            HashMap<MessageQueue, OffsetWrapper> messageQueue = consumeStats.getOffsetTable();
            Iterator iter = messageQueue.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry entry = (Map.Entry) iter.next();
                MessageQueue queue = (MessageQueue) entry.getKey();
                if (!queue.getTopic().startsWith(MixAll.RETRY_GROUP_TOPIC_PREFIX)) {
                    topics.add(queue.getTopic());
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            shutdownDefaultMQAdminExt(defaultMQAdminExt);
        }
        return topics.toArray();
    }


    /**
     * 通过 Topic 来抓取 ConsumerGroup List
     *
     * @param topic
     * @return
     */
    public List fetchAllConsumerByTopic(String topic) {
        DefaultMQAdminExt defaultMQAdminExt = getDefaultMQAdminExt();
        List list = new ArrayList<String>();

        try {
            defaultMQAdminExt.start();
            GroupList temp = defaultMQAdminExt.queryTopicConsumeByWho(topic); // 得到该 Topic 下的所有消费者列表
            if (list == null) {
                return list;
            }
            HashSet<String> groupList = temp.getGroupList();
            if (groupList == null || groupList.size() == 0) {
                return list;

            }
            Iterator iterator = groupList.iterator();
            while (iterator.hasNext()) {
                String consumerGroup = String.valueOf(iterator.next());
                list.add(consumerGroup);

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            shutdownDefaultMQAdminExt(defaultMQAdminExt);
        }

        return list;
    }
}

