package com.alibaba.rocketmq.action;

import com.alibaba.rocketmq.common.protocol.body.ConsumerConnection;
import com.alibaba.rocketmq.common.protocol.body.ProducerConnection;
import com.alibaba.rocketmq.service.ConnectionService;
import org.apache.commons.cli.Option;
import org.apache.commons.lang.StringUtils;
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
 * @date 2014-2-16
 */
@Controller
@RequestMapping("/connection")
public class ConnectionAction extends AbstractAction {

    @Autowired
    ConnectionService connectionService;


    @Override
    protected String getFlag() {
        return "connection_flag";
    }


    @RequestMapping(value = "/consumerConnection.do", method = {RequestMethod.GET, RequestMethod.POST})
    public String consumerConnection(ModelMap map, HttpServletRequest request,
                                     @RequestParam(required = false) String consumerGroup) {
        Collection<Option> options = connectionService.getOptionsForGetConsumerConnection();
        putPublicAttribute(map, "consumerConnection", options, request);
        try {
            // 如果是 get 请求，将不返回数据，直接返回这个页面
            if (request.getMethod().equals(GET) && StringUtils.isBlank(consumerGroup)) {

            }
            if (StringUtils.isNotBlank(consumerGroup)) {
                checkOptions(options);
                ConsumerConnection cc = connectionService.getConsumerConnection(consumerGroup);
                map.put("cc", cc);
            }
            // 如果是 POST 请求，将返回数据及页面
            else if (request.getMethod().equals(POST)) {
                checkOptions(options);
                ConsumerConnection cc = connectionService.getConsumerConnection(consumerGroup);
                map.put("cc", cc);
            }
        } catch (Throwable e) {
            putAlertMsg(e, map);
        }
        return TEMPLATE;
    }


    @RequestMapping(value = "/producerConnection.do", method = {RequestMethod.GET, RequestMethod.POST})
    public String producerConnection(ModelMap map, HttpServletRequest request,
                                     @RequestParam(required = false) String producerGroup, @RequestParam(required = false) String topic) {
        Collection<Option> options = connectionService.getOptionsForGetProducerConnection();
        putPublicAttribute(map, "producerConnection", options, request);
        try {
            if (request.getMethod().equals(GET)) {

            } else if (request.getMethod().equals(POST)) {
                checkOptions(options);
                ProducerConnection pc = connectionService.getProducerConnection(producerGroup, topic);
                map.put("pc", pc);
            } else {
                throwUnknowRequestMethodException(request);
            }
        } catch (Throwable e) {
            putAlertMsg(e, map);
        }
        return TEMPLATE;
    }


    @Override
    protected String getName() {
        return "Connection";
    }
}
