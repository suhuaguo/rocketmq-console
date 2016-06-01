package com.alibaba.rocketmq.service;

import com.alibaba.rocketmq.bo.Monitor;

/**
 * Created with IntelliJ IDEA.
 * User:jiandan
 * Date:2016/5/17.
 * Time:11:35.
 * INFO:监控消费进度
 */
public interface IMonitorService {

    /**
     * 添加监控
     * @param monitor
     * @return
     */
    int addMonitor(Monitor monitor);

    /**
     * 更新监控
     * @param monitor
     * @return
     */
    int updateMonitor(Monitor monitor);

    /**
     * 删除监控
     * @param conTopic
     * @return
     */
    int deleteMonitor(String conTopic);

    /**
     * 启动监控
     * @param conTopic
     * @return
     */
    int startMonitor(String conTopic);

    /**
     * 获取监控
     * @param conTopic
     * @return
     */
    Monitor getMonitor(String conTopic);

}
