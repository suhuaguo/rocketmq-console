package com.alibaba.rocketmq.action;

import com.alibaba.rocketmq.config.ConfigureInitializer;
import com.alibaba.rocketmq.service.MessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created with IntelliJ IDEA.
 * User:jiandan
 * Date:2016/1/26.
 * Time:10:39.
 */

@Controller
@RequestMapping("/message_ajax")
public class MessageAjaxAction {

    private Logger logger = LoggerFactory.getLogger(MessageAction.class);

    @Autowired
    MessageService messageService;

    @Autowired
    ConfigureInitializer configureInitializer;


    /**
     * 消息重新 投递,采用广播模式
     *
     * @param
     * @param msgId
     */
    @RequestMapping(value = "/retryDevilerMsg.do", method = {RequestMethod.GET, RequestMethod.POST})
    public
    @ResponseBody
    String retryDevilerMsg(@RequestParam(required = true) String msgId) {

        try {
            return messageService.deliverMsg(msgId);

        } catch (Throwable t) {
            logger.error(t.getMessage());
        }
        return null;

    }

    /**
     * 消息重新 投递，直接将消息投递给某个消费者
     *
     * @param consumerGroupList
     * @param clientIdList
     * @param msgId
     * @return
     */
    @RequestMapping(value = "/consumeMessageDirectly.do", method = {RequestMethod.GET, RequestMethod.POST})
    public
    @ResponseBody
    String consumeMessageDirectly(String consumerGroupList, String clientIdList, String msgId) {

        String[] CGList = consumerGroupList.split(",");
        String[] CIList = clientIdList.split(",");


        StringBuilder reContent = new StringBuilder(); // 恢复的静态变量

        try {
            for (int i = 0; i < CIList.length; i++) {
                String tmp = messageService.consumeMessageDirectly(CGList[i], CIList[i], msgId);
                reContent.append(tmp).append(";");
            }
            return reContent.substring(0, reContent.length() - 1);

        } catch (Throwable t) {
            logger.error(t.getMessage());
        }
        return "ERROR";

    }


}
