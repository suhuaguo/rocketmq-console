package com.alibaba.rocketmq.action;

import com.alibaba.rocketmq.bo.ConTopicTpsBo;
import com.alibaba.rocketmq.bo.ProTopicTpsBo;
import com.alibaba.rocketmq.config.ConfigureInitializer;
import com.alibaba.rocketmq.constants.DataType;
import com.alibaba.rocketmq.enums.RoundEnums;
import com.alibaba.rocketmq.service.*;
import com.github.abel533.echarts.axis.CategoryAxis;
import com.github.abel533.echarts.axis.ValueAxis;
import com.github.abel533.echarts.code.*;
import com.github.abel533.echarts.data.Data;
import com.github.abel533.echarts.feature.MagicType;
import com.github.abel533.echarts.json.GsonOption;
import com.github.abel533.echarts.series.Line;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User:jiandan
 * Date:2016/1/26.
 * Time:13:56.
 * INFO:Topic 相关。只要是 Ajax 请求
 */
@Controller
@RequestMapping("/fetch_ajax")
public class FetchAjaxtAction {

    @Autowired
    ConfigureInitializer configureInitializer;

    @Autowired
    TopicService topicService;

    @Autowired
    AjaxService ajaxService;

    @Autowired
    ConsumerService consumerService;

    @Autowired
    ProTopicTpsService proTopicTpsService;

    @Autowired
    ConTopicTpsService conTopicTpsService;

    /**
     * 抓取集群中的所有 Topic
     *
     * @return
     */
    @RequestMapping(value = "/fetch_all_topic.do", method = {RequestMethod.GET, RequestMethod.POST})
    public
    @ResponseBody
    List<String> fetchAllTopic() {

        return topicService.fetchAllTopic();

    }


    /**
     * 抓取集群中的所有 Topic
     *
     * @return
     */
    @RequestMapping(value = "/fetch_all_consumer_topic.do", method = {RequestMethod.GET, RequestMethod.POST})
    public
    @ResponseBody
    List<String> fetchAllConsumerByTopic(String topic) {

        return topicService.fetchAllConsumerByTopic(topic);

    }


    /**
     * 抓取集群中的所有 Topic
     *
     * @return
     */
    @RequestMapping(value = "/get_topic_consumer.do", method = {RequestMethod.GET, RequestMethod.POST})
    public
    @ResponseBody
    Object[] getTopicByConsumer(String consumerGroup) {

        return topicService.getTopicByConsumer(consumerGroup);

    }


    /**
     * 抓取集群中的所有 消费者
     *
     * @return
     */
    @RequestMapping(value = "/fetch_all_consumer.do", method = {RequestMethod.GET, RequestMethod.POST})
    public
    @ResponseBody
    List<String> fetchAllConsumer() {

        return consumerService.fetchAllConsumerGroup();

    }


    /**
     * 抓取集群中 consumer 的信息
     *
     * @return
     */
    @RequestMapping(value = "/fetch_consumer_tps.do", method = {RequestMethod.GET, RequestMethod.POST})
    public
    @ResponseBody
    String fetchConsumerTps(@Param("topic") String topic, @Param("consumerGroup") String consumerGroup, @Param("round") int round, @Param("startTime") String startTime, @Param("endTime") String endTime, @Param("type") int type) {
        List<ConTopicTpsBo> conTopicTpsBoList = conTopicTpsService.queryByTime(topic + "@" + consumerGroup, parseToDate(startTime), parseToDate(endTime), RoundEnums.getOffesetByRoundType(round));
        String[] xAis = null;
        if (null != conTopicTpsBoList || conTopicTpsBoList.size() > 0) {
            xAis = new String[conTopicTpsBoList.size()]; // 初始化横轴大小
        }

        String lengend = "消费" + (DataType.DATA_TYPE_TPS == type ? "TPS" : "总量");
        Line line = new Line();
        GsonOption options = drawAxis(getXaisDataConsumer(conTopicTpsBoList), lengend);

        line.smooth(true).name(lengend).data(getDataToConsumer(conTopicTpsBoList, type)).itemStyle();
        // 计算最大值、最小值、平均值
        Data data1 = new Data();
        data1.setName("最大值");
        data1.setType(MarkType.max);
        Data data2 = new Data();
        data2.setName("最小值");
        data2.setType(MarkType.min);

        line.markPoint().data(data1, data2);

        Data data3 = new Data();
        data3.setName("平均值");
        data3.setType(MarkType.average);

        line.markLine().data(data3);

        options.series(line);
        options.toolbox().feature().get("dataView").setShow(false); // 不要显示 dataView
        return options.toString();


    }


    @RequestMapping(value = "/fetch_produer_tps.do", method = {RequestMethod.GET, RequestMethod.POST})
    public
    @ResponseBody
    String fetchProuderTps(@Param("topic") String topic, @Param("round") int round, @Param("startTime") String startTime, @Param("endTime") String endTime, @Param("type") int type) {

        List<ProTopicTpsBo> proTopicTpsBoList = proTopicTpsService.queryByTime(topic, parseToDate(startTime), parseToDate(endTime), RoundEnums.getOffesetByRoundType(round));
        String[] xAis = null;
        if (null != proTopicTpsBoList || proTopicTpsBoList.size() > 0) {
            xAis = new String[proTopicTpsBoList.size()]; // 初始化横轴大小
        }

        String lengend = "发送" + (DataType.DATA_TYPE_TPS == type ? "TPS" : "总量");

        Line line = new Line();
        GsonOption options = drawAxis(getXaisDataProducer(proTopicTpsBoList), lengend);


        line.smooth(true).name(lengend).data(getDataToProducer(proTopicTpsBoList, type)).itemStyle();

        // 计算最大值、最小值、平均值
        Data data1 = new Data();
        data1.setName("最大值");
        data1.setType(MarkType.max);
        Data data2 = new Data();
        data2.setName("最小值");
        data2.setType(MarkType.min);

        line.markPoint().data(data1, data2);

        Data data3 = new Data();
        data3.setName("平均值");
        data3.setType(MarkType.average);

        line.markLine().data(data3);

        options.series(line);

        options.toolbox().feature().get("dataView").setShow(false); // 不要显示 dataView
        return options.toString();

    }


    /**
     * 绘制坐标轴
     *
     * @param category 类目轴
     * @param legend   线的名称
     * @return
     */
    public static GsonOption drawAxis(String[] category, String legend) {
        GsonOption option = new GsonOption();
        option.title().textStyle().fontSize(12);
        option.legend(legend);

        option.toolbox().show(true).feature(Tool.dataView, new MagicType(Magic.line, Magic.bar), Tool.restore, Tool.saveAsImage);

        CategoryAxis categoryAxis = new CategoryAxis();//类目轴
        categoryAxis.type(AxisType.category);
        categoryAxis.axisLabel().formatter("{value} ");
        categoryAxis.data(category);
        categoryAxis.name("时间");
        option.xAxis(categoryAxis);

        ValueAxis valueAxis = new ValueAxis(); // 值轴。就是 y 轴
        valueAxis.type(AxisType.value);
        valueAxis.boundaryGap();
        valueAxis.name("单位/1");
        valueAxis.setScale(true);


        option.yAxis(valueAxis);
        option.tooltip().setTrigger(Trigger.axis);
        return option;

    }


    /**
     * 得到横轴的数据----- 消费者
     *
     * @param conTopicTpsBoList
     * @return
     */
    public String[] getXaisDataConsumer(List<ConTopicTpsBo> conTopicTpsBoList) {
        String[] xAis = new String[conTopicTpsBoList.size()]; // 初始化横轴大小
        int index = 0;
        for (ConTopicTpsBo conTopicTpsBo : conTopicTpsBoList) {
            xAis[index] = conTopicTpsBo.getConTime().substring(0, conTopicTpsBo.getConTime().length() - 5); // 得到横轴的每个点
            index = index + 1;
        }

        return xAis;
    }


    /**
     * 得到横轴的数据 --- 生产者
     *
     * @param proTopicTpsBoList
     * @return
     */
    public String[] getXaisDataProducer(List<ProTopicTpsBo> proTopicTpsBoList) {
        String[] xAis = new String[proTopicTpsBoList.size()]; // 初始化横轴大小
        int index = 0;
        for (ProTopicTpsBo proTopicTpsBo : proTopicTpsBoList) {
            xAis[index] = proTopicTpsBo.getProTime().substring(0, proTopicTpsBo.getProTime().length() - 5); // 得到横轴的每个点
            index = index + 1;
        }

        return xAis;
    }


    /**
     * 得到生产者的 TPS 的数据量
     *
     * @param proTopicTpsBoList
     * @return
     */
    public Double[] getDataToProducer(List<ProTopicTpsBo> proTopicTpsBoList, int type) {
        Double[] data = new Double[proTopicTpsBoList.size()]; // 初始化横轴大小
        int index = 0;


        for (ProTopicTpsBo proTopicTpsBo : proTopicTpsBoList) {
            data[index] = DataType.DATA_TYPE_TPS == type ? proTopicTpsBo.getProTps() : proTopicTpsBo.getProSum(); // 得到横轴的每个点
            index = index + 1;
        }

        return data;
    }


    /**
     * 得到消费者的 TPS 的数据量
     *
     * @param conTopicTpsBoList
     * @return
     */
    public Double[] getDataToConsumer(List<ConTopicTpsBo> conTopicTpsBoList, int type) {
        Double[] data = new Double[conTopicTpsBoList.size()]; // 初始化横轴大小
        int index = 0;


        for (ConTopicTpsBo conTopicTpsBo : conTopicTpsBoList) {
            data[index] = DataType.DATA_TYPE_TPS == type ? conTopicTpsBo.getConTps() : conTopicTpsBo.getConSum(); // 得到横轴的每个点
            index = index + 1;
        }

        return data;
    }


    /**
     * 将 字符串的时间转换为时间戳
     *
     * @param time
     * @return
     */
    public Date parseToDate(String time) {
        Date date = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            date = sdf.parse(time);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return date;
    }


    /**
     * 得到 Topic 的详细信息
     *
     * @param topic
     * @return
     */
    @RequestMapping(value = "/get_topic_detail.do", method = {RequestMethod.GET, RequestMethod.POST}, produces = "text/html;charset=UTF-8")
    public
    @ResponseBody
    String getTopicDetail(@Param("topic") String topic) {

        return ajaxService.getTopicDetail(topic);
    }


    /**
     * 添加改Topic 的详细信息
     *
     * @param topic
     * @return
     */
    @RequestMapping(value = "/add_topic_detail.do", method = {RequestMethod.GET, RequestMethod.POST})
    public
    @ResponseBody
    String addTopicDetail(@Param("topic") String topic, @Param("detail") String detail) {

        int count = ajaxService.addTopicDetail(topic, detail);

        if (count > 0) {
            return "SUCCESS";
        } else {
            return "FAILURE";
        }
    }

}
