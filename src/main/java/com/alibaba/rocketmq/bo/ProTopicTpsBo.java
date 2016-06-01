package com.alibaba.rocketmq.bo;

/**
 * Created by zwj on 2016/1/27.
 */
public class ProTopicTpsBo {
    private long id;
    private long proSum;            //总量
    private double proTps;          //tps
    private double proAvgpt;        //平均tps
    private String proTopic;        //topic
    private String proTime;         // 记录插入时间


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getProSum() {
        return proSum;
    }

    public void setProSum(long proSum) {
        this.proSum = proSum;
    }

    public double getProTps() {
        return proTps;
    }

    public void setProTps(double proTps) {
        this.proTps = proTps;
    }

    public double getProAvgpt() {
        return proAvgpt;
    }

    public void setProAvgpt(double proAvgpt) {
        this.proAvgpt = proAvgpt;
    }

    public String getProTopic() {
        return proTopic;
    }

    public void setProTopic(String proTopic) {
        this.proTopic = proTopic;
    }

    public String getProTime() {
        return proTime;
    }

    public void setProTime(String proTime) {
        this.proTime = proTime;
    }
}
