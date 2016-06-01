package com.alibaba.rocketmq.task;

import com.alibaba.rocketmq.service.TopicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.List;


/**
 * Created by zwj on 2016/1/28.
 * INFO:这里的方法将 每 5 分钟抓取 Topic、Group，这里时间定义长点
 */

@Configuration
@EnableAsync
@EnableScheduling
public class CommonTpsTask {

    @Autowired
    public TopicService topicService;

    public static List<String> topics;
    public static List<String> topicGroups;

    /**
     * 抓取所有的 Topic
     */
    @Scheduled(cron = "5 * *  * * ? ")
    public void fetchAllTopic() {
        topics = topicService.fetchAllTopic();
    }


    @Scheduled(cron = "1 * *  * * ? ")
    public void fetchGroupByTopic() {
        topicGroups = topicService.fetchGroupByTopic();
    }

}
