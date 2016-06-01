package com.alibaba.rocketmq.vo;

import java.sql.Timestamp;

/**
 * Created by zwj on 2016/1/27.
 */
public class ProTopicTpsVo {
    private long id;
    private long proSum; //总量
    private double proTps;//tps
    private double proAvgpt;//平均tps
    private String proTopic;//topic

    private Timestamp proTime;//time
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
    public Timestamp getProTime() {
	return proTime;
    }
    public void setProTime(Timestamp proTime) {
	this.proTime = proTime;
    }
}
