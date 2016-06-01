package com.alibaba.rocketmq.vo;

/**
 * Created by zwj on 2016/1/28.
 */
public class ConTopicTpsVo {
    private long id;
    private long conSum; //总量
    private double conTps;//tps
    private double conAvgpt;//平均tps
    private String conTopic;//topic
    private String conTime;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getConSum() {
        return conSum;
    }

    public void setConSum(long conSum) {
        this.conSum = conSum;
    }

    public double getConTps() {
        return conTps;
    }

    public void setConTps(double conTps) {
        this.conTps = conTps;
    }

    public double getConAvgpt() {
        return conAvgpt;
    }

    public void setConAvgpt(double conAvgpt) {
        this.conAvgpt = conAvgpt;
    }

    public String getConTopic() {
        return conTopic;
    }

    public void setConTopic(String conTopic) {
        this.conTopic = conTopic;
    }

    public String getConTime() {
        return conTime;
    }

    public void setConTime(String conTime) {
        this.conTime = conTime;
    }
}
