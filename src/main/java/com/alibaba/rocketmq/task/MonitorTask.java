package com.alibaba.rocketmq.task;

import com.alibaba.fastjson.JSON;
import com.alibaba.rocketmq.bo.Monitor;
import com.alibaba.rocketmq.common.admin.ConsumeStats;
import com.alibaba.rocketmq.common.admin.OffsetWrapper;
import com.alibaba.rocketmq.common.message.MessageQueue;
import com.alibaba.rocketmq.mapper.MonitorMapper;
import com.alibaba.rocketmq.tools.admin.DefaultMQAdminExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created with IntelliJ IDEA.
 * User:jiandan
 * Date:2016/5/17.
 * Time:16:46.
 * INFO:监控 Task，每 5 分钟跑一次 Task
 * 数据库的信息每 10 分钟读取一次。
 * 如果有修改东西，通过 aop 方式，来更新缓存的信息
 */
@Configuration
@EnableAsync
@EnableScheduling
public class MonitorTask {

    private Logger alert = LoggerFactory.getLogger("alert");

    @Autowired
    public DefaultMQAdminExt defaultMQAdminExt;

    public static Map<String, Monitor> monitorMap = new ConcurrentHashMap<>();

    @Resource
    MonitorMapper monitorMapper;


    // 这个是发到公司的 logstash ，并发送告警
    //@Scheduled(cron = "5 * *  * * ? ")
    public void startMonitor() {
        if (monitorMap.size() == 0) {
            alert.error("监控列表为空");
            return;
        }

        Iterator<Map.Entry<String, Monitor>> iterator = monitorMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Monitor> monitorEntryentry = iterator.next();
            Monitor monitor = monitorEntryentry.getValue();
            System.out.println(JSON.toJSONString(monitor));
            long delayTimestamp = System.currentTimeMillis() - monitor.getDelayTime() * 60 * 1000; // 分钟 * 60 秒 * 1000

            try {
                ConsumeStats consumeStats = defaultMQAdminExt.examineConsumeStats(monitor.getConTopic().split("@")[0]);
                HashMap<MessageQueue, OffsetWrapper> offsetTable = consumeStats.getOffsetTable();

                long diff = consumeStats.computeTotalDiff();

                if (monitor.getDiff() <= diff) {
                    alert.error(monitor.getConTopic() + "消费太缓慢了！diff total:" + diff);
                    continue;
                }

                // 循环取得消费最后的时间值，重试队列的数据还是会消费的
                Iterator iter = offsetTable.entrySet().iterator();
                while (iter.hasNext()) {
                    Map.Entry entry = (Map.Entry) iter.next();
                    OffsetWrapper val = (OffsetWrapper) entry.getValue();
                    if (val.getLastTimestamp() < delayTimestamp && diff >= 1) {  // 可能没有消息了。或者时间太长了，有几条消息没有消费掉
                        alert.error(monitor.getConTopic() + "消费太缓慢了！最慢消费时间点:" + new Date(val.getLastTimestamp()));
                        break;
                    }

                }
            } catch (Exception e) {
                alert.error(e.getMessage());
            }

        }
    }


    @Scheduled(cron = "10 * *  * * ? ")
    public void getMonitorList() {
        List<Monitor> list = monitorMapper.getMonitorList();

        for (Monitor monitor : list) {
            monitorMap.put(monitor.getConTopic(), monitor);
        }
    }


}
