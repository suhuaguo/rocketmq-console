package com.alibaba.rocketmq.service.impl;

import com.alibaba.rocketmq.bo.ConTopicTpsBo;
import com.alibaba.rocketmq.mapper.ConTopicTpsMapper;
import com.alibaba.rocketmq.service.ConTopicTpsService;
import com.alibaba.rocketmq.vo.ConTopicTpsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

/**
 * Created by zwj on 2016/1/28.
 */
@Service
public class ConTopicTpsServiceImpl implements ConTopicTpsService {

    @Autowired
    private ConTopicTpsMapper conTopicTpsMapper;


    @Override public void insert(ConTopicTpsVo conTopicTpsVo) {
	conTopicTpsMapper.insert(conTopicTpsVo);

    }
    @Override public List<ConTopicTpsBo> queryByTime(String topic, Date start, Date end,long offset) {
	return conTopicTpsMapper.queryByTime(topic,start,end,offset);
    }
}
