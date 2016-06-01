package com.alibaba.rocketmq.bo;

/**
 * Created with IntelliJ IDEA.
 * User:jiandan
 * Date:2016/5/17.
 * Time:11:07.
 * INFO:监控报警
 */
public class Monitor {

    // consumerGroup@topic
    private String conTopic;

    // 消息滞后的数量
    private int diff;

    // 滞后当前时间
    private int delayTime;

    // 是否启动监控
    private int status;

    // 最后修改时间
    private long modifyTime;


    public String getConTopic() {
        return conTopic;
    }

    public void setConTopic(String conTopic) {
        this.conTopic = conTopic;
    }

    public int getDiff() {
        return diff;
    }

    public void setDiff(int diff) {
        this.diff = diff;
    }

    public int getDelayTime() {
        return delayTime;
    }

    public void setDelayTime(int delayTime) {
        this.delayTime = delayTime;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public long getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(long modifyTime) {
        this.modifyTime = modifyTime;
    }
}
