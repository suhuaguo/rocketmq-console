package com.alibaba.rocketmq.task;

import com.alibaba.rocketmq.common.protocol.body.BrokerStatsData;
import com.alibaba.rocketmq.config.ConfigureInitializer;
import com.alibaba.rocketmq.service.ConTopicTpsService;
import com.alibaba.rocketmq.store.stats.BrokerStatsManager;
import com.alibaba.rocketmq.tools.admin.DefaultMQAdminExt;
import com.alibaba.rocketmq.vo.ConTopicTpsVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * Created by zwj on 2016/1/28.
 * INFO:消费者 TPS 统计
 *
 * @Scheduled(cron = "1 * *  * * ? ") 表示每  1 分钟执行一次
 */
@Configuration
@EnableAsync
@EnableScheduling
public class ConTopicTpsTask extends CommonTpsTask {

    private static final Logger logger = LoggerFactory.getLogger(ConTopicTpsTask.class);
    @Autowired
    private ConTopicTpsService conTopicTpsService;

    @Autowired
    public DefaultMQAdminExt defaultMQAdminExt;

    @Autowired
    public ConfigureInitializer configureInitializer;


    @Scheduled(cron = "1 * *  * * ? ")
    public void getSats() {

        if (null == topicGroups || topicGroups.size() == 0 || null == defaultMQAdminExt) {
            logger.warn("topicGroups is null ");
            return;
        }

        for (String arg : topicGroups) {
            try {

                BrokerStatsData brokerStatsData = defaultMQAdminExt.ViewBrokerStatsData(configureInitializer.getBrokerAddr(), BrokerStatsManager.GROUP_GET_NUMS, arg);
                conTopicTpsService.insert(bulidTopicTpsVo(brokerStatsData, arg));
                logger.info(arg + ":" + brokerStatsData.toJson());
            } catch (Exception e) {

                logger.warn(arg + " consumer get tps error ");
            }
        }

    }


    public ConTopicTpsVo bulidTopicTpsVo(BrokerStatsData b, String topic) {
        ConTopicTpsVo conTopicTpsVo = new ConTopicTpsVo();
        conTopicTpsVo.setConAvgpt(b.getStatsMinute().getAvgpt());
        conTopicTpsVo.setConSum(b.getStatsMinute().getSum());
        conTopicTpsVo.setConTps(b.getStatsMinute().getTps());
        conTopicTpsVo.setConTopic(topic);
        return conTopicTpsVo;
    }


}
