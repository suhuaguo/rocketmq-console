package com.alibaba.rocketmq.service;

import com.alibaba.rocketmq.bo.ConTopicTpsBo;
import com.alibaba.rocketmq.vo.ConTopicTpsVo;

import java.util.Date;
import java.util.List;

/**
 * Created by zwj on 2016/1/28.
 * Consumer TPS
 */
public interface ConTopicTpsService {


    public void insert(ConTopicTpsVo proTopicTpsVo);

    /**
     * 统计消费者的 TPS
     *
     * @param topic
     * @param start  开始时间
     * @param end    结束时间
     * @param offset 偏移量
     * @return
     */
    public List<ConTopicTpsBo> queryByTime(String topic, Date start, Date end, long offset);
}
