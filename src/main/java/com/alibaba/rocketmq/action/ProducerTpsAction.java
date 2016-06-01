package com.alibaba.rocketmq.action;

/**
 * Created with IntelliJ IDEA.
 * User:jiandan
 * Date:2016/1/26.
 * Time:13:57.
 * INFO:每一个 Topic 对应的  生产者的 TPS .
 */
public class ProducerTpsAction extends AbstractAction {

    @Override
    protected String getFlag() {
        return "producer_tps_tag";
    }

    @Override
    protected String getName() {
        return "producer_tps";
    }
}
