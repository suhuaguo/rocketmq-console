package com.alibaba.rocketmq.service;

/**
 * Created with IntelliJ IDEA.
 * User:jiandan
 * Date:2016/3/14.
 * Time:15:55.
 * INFO:所有的 Ajax 的请求，先暂时放在这里
 */
public interface AjaxService {


    /**
     * 获取 Topic 的详细作用
     *
     * @param topic
     */
    public String getTopicDetail(String topic);


    /**
     * 添加 Topic 的详细信息
     * @param topic
     * @param detail
     * @return
     */
    public int addTopicDetail(String topic, String detail);
}
