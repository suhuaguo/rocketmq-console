package com.alibaba.rocketmq.bo;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User:jiandan
 * Date:2016/1/27.
 * Time:11:10.
 * INFO:Tps 的基本数据模型
 *
 */
public class TpsBo implements Serializable {
    private static final long serialVersionUID = 5335080214924412762L;

    // Tpoic
    private String topic;

    // 采集到的 Tps
    private long tps;

    // 采集类型
    private int type;

    // 消费者
    private String consumerId;

    // 插入的时间
    private long time;

    // 采集的周期 1:`0 2:30
    private int roud;


    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public long getTps() {
        return tps;
    }

    public void setTps(long tps) {
        this.tps = tps;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getConsumerId() {
        return consumerId;
    }

    public void setConsumerId(String consumerId) {
        this.consumerId = consumerId;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public int getRoud() {
        return roud;
    }

    public void setRoud(int roud) {
        this.roud = roud;
    }
}
