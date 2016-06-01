package com.alibaba.rocketmq.service.impl;

import com.alibaba.rocketmq.bo.ProTopicTpsBo;
import com.alibaba.rocketmq.mapper.ProTopicTpsMapper;
import com.alibaba.rocketmq.service.ProTopicTpsService;
import com.alibaba.rocketmq.vo.ProTopicTpsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * Created by zwj on 2016/1/27.
 */
@Service
public class ProTopicTpsServiceImpl implements ProTopicTpsService {

   @Autowired
   private ProTopicTpsMapper proTopicTpsMapper;


    @Override public void insert(ProTopicTpsVo proTopicTpsVo) {
	proTopicTpsMapper.insert(proTopicTpsVo);

    }

    @Override public List<ProTopicTpsBo> queryByTime(String topic, Date start, Date end,long offset) {
	return proTopicTpsMapper.queryByTime(topic,start,end,offset);
    }
}
