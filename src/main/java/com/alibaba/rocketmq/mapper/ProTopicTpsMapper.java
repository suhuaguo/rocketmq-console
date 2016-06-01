package com.alibaba.rocketmq.mapper;

import com.alibaba.rocketmq.bo.ProTopicTpsBo;
import com.alibaba.rocketmq.vo.ProTopicTpsVo;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * Created by zwj on 2016/1/27.
 */
public interface ProTopicTpsMapper {


    void insert(ProTopicTpsVo proTopicTpsVo);

    List<ProTopicTpsBo> queryByTime(@Param("topic") String topic, @Param("startTime") Date startTime, @Param("endTime") Date endTime, @Param("offset") long offset);


}
