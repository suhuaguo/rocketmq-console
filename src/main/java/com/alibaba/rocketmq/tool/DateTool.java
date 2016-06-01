package com.alibaba.rocketmq.tool;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User:jiandan
 * Date:2016/1/29.
 * Time:11:25.
 */
public class DateTool {

    /**
     * 将 将时间戳 转换为时间,直接字符串显示
     *
     * @param time
     * @return
     */
    public static final String parseToDate(long time) {
        String date = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            date = sdf.format(new Date(time));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return date;
    }

}
