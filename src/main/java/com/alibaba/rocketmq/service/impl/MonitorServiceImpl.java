package com.alibaba.rocketmq.service.impl;

import com.alibaba.rocketmq.bo.Monitor;
import com.alibaba.rocketmq.mapper.MonitorMapper;
import com.alibaba.rocketmq.service.IMonitorService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Created with IntelliJ IDEA.
 * User:jiandan
 * Date:2016/5/17.
 * Time:11:38.
 * INFO:监控
 */
@Service
public class MonitorServiceImpl implements IMonitorService {

    @Resource
    MonitorMapper monitorMapper;

    @Override
    public int addMonitor(Monitor monitor) {
        return monitorMapper.addMonitor(monitor);
    }

    @Override
    public int updateMonitor(Monitor monitor) {
        return monitorMapper.updateMonitor(monitor);
    }

    @Override
    public int deleteMonitor(String conTopic) {
        return monitorMapper.deleteMonitor(conTopic);
    }

    @Override
    public int startMonitor(String conTopic) {
        return monitorMapper.startMonitor(conTopic);
    }

    @Override
    public Monitor getMonitor(String conTopic) {
        return monitorMapper.getMonitor(conTopic);
    }
}
