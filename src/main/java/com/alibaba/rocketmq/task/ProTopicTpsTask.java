package com.alibaba.rocketmq.task;

import com.alibaba.rocketmq.common.protocol.body.BrokerStatsData;
import com.alibaba.rocketmq.config.ConfigureInitializer;
import com.alibaba.rocketmq.service.ProTopicTpsService;
import com.alibaba.rocketmq.store.stats.BrokerStatsManager;
import com.alibaba.rocketmq.tools.admin.DefaultMQAdminExt;
import com.alibaba.rocketmq.vo.ProTopicTpsVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * Created by zwj on 2016/1/28.
 * INFO:统计生产者的 TPS
 *
 * @Scheduled(cron = "1 * *  * * ? ") 表示每  1 分钟执行一次
 */
@Configuration
@EnableAsync
@EnableScheduling
public class ProTopicTpsTask extends CommonTpsTask {
    private static final Logger logger = LoggerFactory.getLogger(ConTopicTpsTask.class);

    @Autowired
    private ProTopicTpsService proTopicTpsService;

    @Autowired
    public DefaultMQAdminExt defaultMQAdminExt;

    @Autowired
    public ConfigureInitializer configureInitializer;

    @Scheduled(cron = "1 * *  * * ? ")
    public void getSats() {

        if (null == topics || topics.size() == 0 || null == defaultMQAdminExt) {
            logger.warn("topics is null -");
            return;
        }
        for (String arg : topics) {
            try {

                BrokerStatsData brokerStatsData = defaultMQAdminExt.ViewBrokerStatsData(configureInitializer.getBrokerAddr(), BrokerStatsManager.TOPIC_PUT_NUMS, arg);
                proTopicTpsService.insert(bulidTopicTpsVo(brokerStatsData, arg));
                logger.info(arg + ":" + brokerStatsData.toJson());

            } catch (Exception e) {

                logger.warn(arg + " producer get tps error ");
            }
        }
    }


    public ProTopicTpsVo bulidTopicTpsVo(BrokerStatsData b, String topic) {
        ProTopicTpsVo proTopicTpsVo = new ProTopicTpsVo();
        proTopicTpsVo.setProAvgpt(b.getStatsMinute().getAvgpt());
        proTopicTpsVo.setProSum(b.getStatsMinute().getSum());
        proTopicTpsVo.setProTps(b.getStatsMinute().getTps());
        proTopicTpsVo.setProTopic(topic);
        return proTopicTpsVo;
    }


}
