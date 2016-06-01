package com.alibaba.rocketmq.mapper;

import org.apache.ibatis.annotations.Param;

/**
 * Created with IntelliJ IDEA.
 * User:jiandan
 * Date:2016/3/14.
 * Time:15:57.
 * INFO:Topic 的相关信息
 */
public interface TopicServiceMapper {

    /**
     * 获取 Topic 的详细作用
     *
     * @param topic
     */
    public String getTopicDetail(@Param("topic") String topic);

    /**
     * 添加 Topic 的详细作用
     *
     * @param topic
     * @param detail
     * @return
     */
    public int addTopicDetail(@Param("topic") String topic, @Param("detail") String detail);

    /**
     * 判断记录是否存在
     * @param topic
     * @return
     */
    public int getCount(@Param("topic") String topic);


    /**
     * 更新 Topic 的详细作用
     *
     * @param topic
     * @param detail
     * @return
     */
    public int updateTopicDetail(@Param("topic") String topic, @Param("detail") String detail);

}
