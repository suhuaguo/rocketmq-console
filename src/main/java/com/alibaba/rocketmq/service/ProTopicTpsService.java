package com.alibaba.rocketmq.service;

import com.alibaba.rocketmq.bo.ProTopicTpsBo;
import com.alibaba.rocketmq.vo.ProTopicTpsVo;

import java.util.Date;
import java.util.List;

/**
 * Created by zwj on 2016/1/27.
 */

/**
 * Proududer  TPS
 */
public interface ProTopicTpsService {

    public void insert(ProTopicTpsVo proTopicTpsVo);

    /**
     * 通过时间来查询 生产者的 TPS
     * @param topic
     * @param start 开始时间
     * @param end 结束时间
     * @param offset 偏移量
     * @return
     */
    public List<ProTopicTpsBo> queryByTime(String topic,Date start,Date end,long offset);



}
