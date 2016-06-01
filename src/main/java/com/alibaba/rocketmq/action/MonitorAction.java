package com.alibaba.rocketmq.action;

import com.alibaba.rocketmq.bo.Monitor;
import com.alibaba.rocketmq.service.IMonitorService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

/**
 * Created with IntelliJ IDEA.
 * User:jiandan
 * Date:2016/5/17.
 * Time:13:27.
 * INFO:监控
 */

@Controller
@RequestMapping("/monitor")
public class MonitorAction {

    @Resource
    IMonitorService monitorService;

    /**
     * 更新监控或者添加
     *
     * @param monitor
     * @return
     */
    @RequestMapping(value = "/updateMonitor.do", method = {RequestMethod.GET, RequestMethod.POST})
    public
    @ResponseBody
    int updateMonitor(Monitor monitor) {
        Monitor tmp = monitorService.getMonitor(monitor.getConTopic());
        if (null == tmp) {
            monitorService.addMonitor(monitor);
        } else {
            monitorService.updateMonitor(monitor);
        }
        return 1;
    }

    /**
     * 删除监控
     *
     * @param conTopic
     * @return
     */
    @RequestMapping(value = "/deleteMonitor.do", method = {RequestMethod.GET, RequestMethod.POST})
    public
    @ResponseBody
    int deleteMonitor(String conTopic) {
        return monitorService.deleteMonitor(conTopic);
    }


    /**
     * 开启监控
     *
     * @param conTopic
     * @return
     */
    @RequestMapping(value = "/startMonitor.do", method = {RequestMethod.GET, RequestMethod.POST})
    public
    @ResponseBody
    int startMonitor(String conTopic) {
        return monitorService.startMonitor(conTopic);
    }


    /**
     * 获取监控
     *
     * @param conTopic
     * @return
     */
    @RequestMapping(value = "/getMonitor.do", method = {RequestMethod.GET, RequestMethod.POST})
    public
    @ResponseBody
    Monitor getMonitor(String conTopic) {
        Monitor tmp = monitorService.getMonitor(conTopic);
        return tmp;
    }
}
