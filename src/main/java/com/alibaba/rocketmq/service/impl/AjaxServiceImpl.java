package com.alibaba.rocketmq.service.impl;

import com.alibaba.rocketmq.mapper.TopicServiceMapper;
import com.alibaba.rocketmq.service.AjaxService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created with IntelliJ IDEA.
 * User:jiandan
 * Date:2016/3/14.
 * Time:15:56.
 * INFO:所有的 Ajax 的操作
 */
@Service
public class AjaxServiceImpl implements AjaxService {

    @Autowired
    TopicServiceMapper topicServiceMapper;


    @Override
    public String getTopicDetail(String topic) {

        return topicServiceMapper.getTopicDetail(topic);
    }


    @Override
    public int addTopicDetail(String topic, String detail) {

        int count = topicServiceMapper.getCount(topic);
        if (count == 0) {
            return topicServiceMapper.addTopicDetail(topic, detail);

        } else {
            return topicServiceMapper.updateTopicDetail(topic, detail);
        }

    }
}
