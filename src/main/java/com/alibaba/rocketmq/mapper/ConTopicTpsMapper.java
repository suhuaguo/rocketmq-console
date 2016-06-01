package com.alibaba.rocketmq.mapper;

import com.alibaba.rocketmq.bo.ConTopicTpsBo;
import com.alibaba.rocketmq.vo.ConTopicTpsVo;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * Created by zwj on 2016/1/28.
 */
public interface ConTopicTpsMapper {

    void insert(ConTopicTpsVo conTopicTpsVo);

    List<ConTopicTpsBo> queryByTime(@Param("topic") String topic, @Param("startTime") Date startTime, @Param("endTime") Date endTime, @Param("offset") long offset);
}
