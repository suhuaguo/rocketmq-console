package com.alibaba.rocketmq.mapper;

import com.alibaba.rocketmq.bo.Monitor;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User:jiandan
 * Date:2016/5/17.
 * Time:11:39.
 * INFO:监控
 */
public interface MonitorMapper {


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

    /**
     * 获取 开启监控的信息列表
     * @return
     */
    List<Monitor> getMonitorList();
}
