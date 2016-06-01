package com.alibaba.rocketmq.enums;

/**
 * Created with IntelliJ IDEA.
 * User:jiandan
 * Date:2016/1/28.
 * Time:14:49.
 * INFO: 数据采集周期枚举类型
 */
public enum RoundEnums {

    // 如果采集周期是 1 分钟，那么，偏移量是：1*60*1000
    ROUND_ONE_MINUTE(1, 1 * 60 * 1000),
    // 10 分钟
    ROUND_TEN_MINUTE(2, 10 * 60 * 1000),
    // 30 分钟
    ROUND_THIRTY_MINUTE(3, 30 * 60 * 100),
    // 1 个小时
    ROUND_ONE_HOUR(4, 1 * 60 * 60 * 1000),
    // 1 天
    ROUND_ONE_DAY(5, 24 * 60 * 60 * 1000);


    public static long getOffesetByRoundType(int roundType) {
        for (RoundEnums roundEnums : RoundEnums.values()) {
            if (roundEnums.getRoundType() == roundType) {
                return roundEnums.getOffeset();
            }
        }
        return 0;
    }


    RoundEnums(int roundType, long offeset) {
        this.roundType = roundType;
        this.offeset = offeset;
    }

    // 数据采集周期类型：1 分钟  1 个小时  1 天
    public int roundType;

    public long offeset;


    public int getRoundType() {
        return roundType;
    }

    public void setRoundType(int roundType) {
        this.roundType = roundType;
    }

    public long getOffeset() {
        return offeset;
    }

    public void setOffeset(long offeset) {
        this.offeset = offeset;
    }
}
