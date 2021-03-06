package com.alibaba.rocketmq.action;

import com.alibaba.rocketmq.common.Table;
import com.alibaba.rocketmq.config.ConfigureInitializer;
import com.alibaba.rocketmq.service.MessageService;
import org.apache.commons.cli.Option;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;


/**
 * @author yankai913@gmail.com
 * @date 2014-2-17
 */
@Controller
@RequestMapping("/message")
public class MessageAction extends AbstractAction {

    private Logger logger = LoggerFactory.getLogger(MessageAction.class);

    @Autowired
    MessageService messageService;

    @Autowired
    ConfigureInitializer configureInitializer;


    protected String getFlag() {
        return "message_flag";
    }


    /**
     * 通过 消息ID 来查询消息
     *
     * @param map
     * @param request
     * @param msgId
     * @return
     */
    @RequestMapping(value = "/queryMsgById.do", method = {RequestMethod.GET, RequestMethod.POST})
    public String queryMsgById(ModelMap map, HttpServletRequest request,
                               @RequestParam(required = false) String msgId) {

        Collection<Option> options = messageService.getOptionsForQueryMsgById();
        //Bug fix.
        putPublicAttribute(map, "queryMsgById", options, request);
        try {
            if (request.getMethod().equals(GET)) {

            }
            if (StringUtils.isNotBlank(msgId)) {
                checkOptions(options);
                Table table = messageService.queryMsgById(msgId);
                putTable(map, table);
            } else if (request.getMethod().equals(POST)) {
                checkOptions(options);
                Table table = messageService.queryMsgById(msgId);
                putTable(map, table);
            }
        } catch (Throwable t) {
            putAlertMsg(t, map);
        }
        return TEMPLATE;
    }


    /**
     * 通过 Key 来查询消息
     *
     * @param map
     * @param request
     * @param topic
     * @param msgKey
     * @param fallbackHours
     * @return
     */
    @RequestMapping(value = "/queryMsgByKey.do", method = {RequestMethod.GET, RequestMethod.POST})
    public String queryMsgByKey(ModelMap map, HttpServletRequest request,
                                @RequestParam(required = false) String topic, @RequestParam(required = false) String msgKey,
                                @RequestParam(required = false) String fallbackHours) {
        Collection<Option> options = messageService.getOptionsForQueryMsgByKey();
        putPublicAttribute(map, "queryMsgByKey", options, request);
        try {
            if (request.getMethod().equals(GET)) {

            } else if (request.getMethod().equals(POST)) {
                checkOptions(options);
                Table table = messageService.queryMsgByKey(topic, msgKey, fallbackHours);
                putTable(map, table);
            } else {
                throwUnknowRequestMethodException(request);
            }
        } catch (Throwable t) {
            putAlertMsg(t, map);
        }
        return TEMPLATE;
    }


    /**
     * 通过 offset 来查看消息
     *
     * @param map
     * @param request
     * @param topic
     * @param brokerName
     * @param queueId
     * @param offset
     * @return
     */
    @RequestMapping(value = "/queryMsgByOffset.do", method = {RequestMethod.GET, RequestMethod.POST})
    public String queryMsgByOffset(ModelMap map, HttpServletRequest request,
                                   @RequestParam(required = false) String topic, @RequestParam(required = false) String brokerName,
                                   @RequestParam(required = false) String queueId, @RequestParam(required = false) String offset) {
        Collection<Option> options = messageService.getOptionsForQueryMsgByOffset();
        putPublicAttribute(map, "queryMsgByOffset", options, request);
        try {
            if (request.getMethod().equals(GET)) {

            } else if (request.getMethod().equals(POST)) {
                checkOptions(options);
                Table table = messageService.queryMsgByOffset(topic, brokerName, queueId, offset);
                putTable(map, table);
            } else {
                throwUnknowRequestMethodException(request);
            }
        } catch (Throwable t) {
            putAlertMsg(t, map);
        }
        return TEMPLATE;
    }


    @Override
    protected String getName() {
        return "Message";
    }
}
