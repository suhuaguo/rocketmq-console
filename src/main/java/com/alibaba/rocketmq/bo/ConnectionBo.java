package com.alibaba.rocketmq.bo;

/**
 * Created with IntelliJ IDEA.
 * User:jiandan
 * Date:2016/5/18.
 * Time:17:32.
 * INFO:消费者链接情况 bo
 */
public class ConnectionBo {

    // 客户端的 ip
    public String clientId;

    // groupName
    public String groupName;

    // topic
    public String topic;

    // tag
    public String tag;

    // 消费轨迹
    public String track;

    public ConnectionBo(String clientId, String groupName, String track) {
        this.clientId = clientId;
        this.groupName = groupName;
        this.track = track;
    }

    public String getTrack() {
        return track;
    }

    public void setTrack(String track) {
        this.track = track;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}
