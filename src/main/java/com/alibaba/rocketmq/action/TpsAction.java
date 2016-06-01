package com.alibaba.rocketmq.action;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Created with IntelliJ IDEA.
 * User:jiandan
 * Date:2016/1/26.
 * Time:13:57.
 * INFO:
 *      消费者：每个消费组 对应该 Topic 中的 TPS
 *      生产者：每一个 Topic 对应的  生产者的 TPS .
 */

@Controller
@RequestMapping("/tps")
public class TpsAction extends AbstractAction {


    /**
     * 显示所有消费组
     *
     * @param map
     * @return
     */
    @RequestMapping(value = "/consumerGrouupTps.do", method = {RequestMethod.GET, RequestMethod.POST})
    public String consumerGrouupTps(ModelMap map) {
        putPublicAttribute(map, "consumerGrouupTps");
        return TEMPLATE;
    }

    /**
     * 显示所有消费组
     *
     * @param map
     * @return
     */
    @RequestMapping(value = "/producerTps.do", method = {RequestMethod.GET, RequestMethod.POST})
    public String producerTps(ModelMap map) {
        putPublicAttribute(map, "producerTps");
        return TEMPLATE;
    }


    /**
     * 删除监控，将监控置于无效
     *
     * @param map
     * @return
     */
    @RequestMapping(value = "/deleteMonitor.do", method = {RequestMethod.GET, RequestMethod.POST})
    public String deleteMonitor(ModelMap map) {
        putPublicAttribute(map, "producerTps");
        return TEMPLATE;
    }























    @Override
    protected String getFlag() {
        return "tps_flag";
    }

    @Override
    protected String getName() {
        return "Tps";
    }
}
