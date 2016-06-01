package com.alibaba.rocketmq.service;

import com.alibaba.rocketmq.client.QueryResult;
import com.alibaba.rocketmq.client.consumer.DefaultMQPullConsumer;
import com.alibaba.rocketmq.client.consumer.PullResult;
import com.alibaba.rocketmq.client.producer.DefaultMQProducer;
import com.alibaba.rocketmq.client.producer.SendResult;
import com.alibaba.rocketmq.common.MixAll;
import com.alibaba.rocketmq.common.Table;
import com.alibaba.rocketmq.common.UtilAll;
import com.alibaba.rocketmq.common.message.Message;
import com.alibaba.rocketmq.common.message.MessageExt;
import com.alibaba.rocketmq.common.message.MessageQueue;
import com.alibaba.rocketmq.common.protocol.body.Connection;
import com.alibaba.rocketmq.common.protocol.body.ConsumeMessageDirectlyResult;
import com.alibaba.rocketmq.common.protocol.body.ConsumerConnection;
import com.alibaba.rocketmq.remoting.common.RemotingHelper;
import com.alibaba.rocketmq.tools.admin.DefaultMQAdminExt;
import com.alibaba.rocketmq.tools.admin.api.MessageTrack;
import com.alibaba.rocketmq.tools.command.message.QueryMsgByIdSubCommand;
import com.alibaba.rocketmq.tools.command.message.QueryMsgByKeySubCommand;
import com.alibaba.rocketmq.tools.command.message.QueryMsgByOffsetSubCommand;
import com.alibaba.rocketmq.validate.CmdTrace;
import org.apache.commons.cli.Option;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;


/**
 * @author yankai913@gmail.com
 * @date 2014-2-17
 */
@Service
public class MessageService extends AbstractService {

    static final Logger logger = LoggerFactory.getLogger(MessageService.class);

    @Autowired
    ConnectionService connectionService;
    @Autowired
    public DefaultMQAdminExt defaultMQAdminExt;

    static final QueryMsgByIdSubCommand queryMsgByIdSubCommand = new QueryMsgByIdSubCommand();


    public Collection<Option> getOptionsForQueryMsgById() {
        return getOptions(queryMsgByIdSubCommand);
    }


    @CmdTrace(cmdClazz = QueryMsgByIdSubCommand.class)
    public Table queryMsgById(String msgId) throws Throwable {
        Throwable t = null;
        Map<String, String> map = new LinkedHashMap<String, String>();

        try {
            MessageExt msg = defaultMQAdminExt.viewMessage(msgId);
            String bodyTmpFilePath = createBodyFile(msg);

            map.put("Topic", msg.getTopic());

            map.put("Tags", "[" + msg.getTags() + "]");

            map.put("Keys", "[" + msg.getKeys() + "]");

            map.put("Message Body", new String(msg.getBody()).toString()); // 消息体的内容,有个 S 字符串在里面

            map.put("Queue ID", String.valueOf(msg.getQueueId()));

            map.put("Queue Offset", String.valueOf(msg.getQueueOffset()));

            map.put("CommitLog Offset", String.valueOf(msg.getCommitLogOffset()));

            map.put("Born Timestamp", UtilAll.timeMillisToHumanString2(msg.getBornTimestamp()));  //消息在客户端创建时间戳

            map.put("Store Timestamp", UtilAll.timeMillisToHumanString2(msg.getStoreTimestamp())); // 消息在服务器存储时间戳

            map.put("Born Host", RemotingHelper.parseSocketAddressAddr(msg.getBornHost()));

            map.put("Store Host", RemotingHelper.parseSocketAddressAddr(msg.getStoreHost()));

            map.put("System Flag", String.valueOf(msg.getSysFlag()));

            map.put("Properties", msg.getProperties() != null ? msg.getProperties().toString() : "");

            map.put("Message Body Path", bodyTmpFilePath);


            Table table = Table.Map2VTable(map);


            List<MessageTrack> messageTrackList = defaultMQAdminExt.messageTrackDetail(msg); // 查看消息的跟踪情况

            // 消息的消费状态,由于该消费者有  N 个人消息，所以，，得罗列出来
            for (MessageTrack messageTrack : messageTrackList) {
                String groupName = messageTrack.getConsumerGroup();
                if (messageTrack.getTrackType().name() == "UNKNOW_EXCEPTION") continue;


                ConsumerConnection consumerConnection = connectionService.getConsumerConnection(groupName);
                HashSet<Connection> connectionSet = consumerConnection.getConnectionSet();
                Iterator<Connection> it = connectionSet.iterator();

                while (it.hasNext()) {
                    Connection connection = it.next();
                    String clientId = connection.getClientId();
                    table.addExtData(clientId + ";" + groupName, messageTrack.getTrackType().name()); // <clientAddr,'groupName,track'> and more
                }


            }


            return table;
        } catch (Throwable e) {
            logger.error(e.getMessage(), e);
            t = e;
        }
        throw t;
    }


    private String createBodyFile(MessageExt msg) throws IOException {
        DataOutputStream dos = null;

        try {
            String bodyTmpFilePath = "/tmp/rocketmq/msgbodys";
            File file = new File(bodyTmpFilePath);
            if (!file.exists()) {
                file.mkdirs();
            }
            bodyTmpFilePath = bodyTmpFilePath + "/" + msg.getMsgId();
            dos = new DataOutputStream(new FileOutputStream(bodyTmpFilePath));
            dos.write(msg.getBody());
            return bodyTmpFilePath;
        } finally {
            if (dos != null)
                dos.close();
        }
    }

    static final QueryMsgByKeySubCommand queryMsgByKeySubCommand = new QueryMsgByKeySubCommand();


    public Collection<Option> getOptionsForQueryMsgByKey() {
        return getOptions(queryMsgByKeySubCommand);
    }


    @CmdTrace(cmdClazz = QueryMsgByKeySubCommand.class)
    public Table queryMsgByKey(String topicName, String msgKey, String fallbackHours) throws Throwable {
        Throwable t = null;
        try {
            long h = 0;
            if (StringUtils.isNotBlank(fallbackHours)) {
                h = Long.parseLong(fallbackHours);
            }
            long end = System.currentTimeMillis() - (h * 60 * 60 * 1000);
            long begin = end - (6 * 60 * 60 * 1000); // 6 个小时前的消息
            QueryResult queryResult = defaultMQAdminExt.queryMessage(topicName, msgKey, 32, begin, end);

            // System.out.printf("%-50s %-4s  %s\n",//
            // "#Message ID",//
            // "#QID",//
            // "#Offset");
            String[] thead = new String[]{"#Message ID", "#QID", "#Offset"};
            int row = queryResult.getMessageList().size();
            Table table = new Table(thead, row);

            for (MessageExt msg : queryResult.getMessageList()) {
                String[] data =
                        new String[]{msg.getMsgId(), String.valueOf(msg.getQueueId()),
                                String.valueOf(msg.getQueueOffset())};
                table.insertTR(data);
                // System.out.printf("%-50s %-4d %d\n", msg.getMsgId(),
                // msg.getQueueId(), msg.getQueueOffset());
            }
            return table;
        } catch (Throwable e) {
            logger.error(e.getMessage(), e);
            t = e;
        }
        throw t;
    }

    static final QueryMsgByOffsetSubCommand queryMsgByOffsetSubCommand = new QueryMsgByOffsetSubCommand();


    public Collection<Option> getOptionsForQueryMsgByOffset() {
        return getOptions(queryMsgByOffsetSubCommand);
    }


    @CmdTrace(cmdClazz = QueryMsgByOffsetSubCommand.class)
    public Table queryMsgByOffset(String topicName, String brokerName, String queueId, String offset)
            throws Throwable {
        Throwable t = null;
        DefaultMQPullConsumer defaultMQPullConsumer = new DefaultMQPullConsumer(MixAll.TOOLS_CONSUMER_GROUP);

        defaultMQPullConsumer.setInstanceName(Long.toString(System.currentTimeMillis()));

        try {
            MessageQueue mq = new MessageQueue();
            mq.setTopic(topicName);
            mq.setBrokerName(brokerName);
            mq.setQueueId(Integer.parseInt(queueId));

            PullResult pullResult = defaultMQPullConsumer.pull(mq, "*", Long.parseLong(offset), 1);
            if (pullResult != null) {
                switch (pullResult.getPullStatus()) {
                    case FOUND:
                        Table table = queryMsgById(pullResult.getMsgFoundList().get(0).getMsgId());
                        return table;
                    case NO_MATCHED_MSG:
                    case NO_NEW_MSG:
                    case OFFSET_ILLEGAL:
                    default:
                        break;
                }
            } else {
                throw new IllegalStateException("pullResult is null");
            }
        } catch (Throwable e) {
            logger.error(e.getMessage(), e);
            t = e;
        } finally {
            defaultMQPullConsumer.shutdown();
        }
        throw t;
    }


    /**
     * 重新投递消息
     *
     * @param msgId
     * @return
     */
    public String deliverMsg(String msgId) {

        try {
            MessageExt message = defaultMQAdminExt.viewMessage(msgId);

            String topic = message.getTopic();
            String tag = message.getTags();
            String keys = message.getKeys();

            // 发送消息
            DefaultMQProducer producer = new DefaultMQProducer("defualt_producer");
            producer.setInstanceName("console_producer" + System.currentTimeMillis());
            producer.setNamesrvAddr(configureInitializer.getNamesrvAddr());

            producer.start(); // 发送者启动

            Message msg = new Message(topic, tag, message.getBody());
            msg.setKeys(keys);

            SendResult sendResult = producer.send(msg);
            System.out.println("sdfsdf----" + sendResult.getMsgId());

            return sendResult.getMsgId();


        } catch (Throwable e) {
            logger.error(e.getMessage(), e);

        }

        return null;
    }


    /**
     * 直接将消息投递给某个消费者
     *
     * @param consumerGroup
     * @param clientId      ip@进程号
     * @param msgId
     * @return
     */
    public String consumeMessageDirectly(String consumerGroup, String clientId, String msgId) {
        try {
            ConsumeMessageDirectlyResult directlyResult = defaultMQAdminExt.consumeMessageDirectly(consumerGroup, clientId, msgId);

            String result = directlyResult.getConsumeResult().name();
            long spentTimeMills = directlyResult.getSpentTimeMills(); // 投递花费的时间

            StringBuilder builder = new StringBuilder();
            return builder.append(clientId).append(" ,").append(consumerGroup).append(" ,").append(result).append(" ,time:" + spentTimeMills+" ms").toString();
        } catch (Exception e) {
            logger.error(e.getMessage());
        }

        return "ERROR";
    }
}
