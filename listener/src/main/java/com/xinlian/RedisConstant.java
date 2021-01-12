package com.xinlian;

public interface RedisConstant {
    /**
     * 挖矿前缀
     */
    String MINING = "mining";
    /**
     * 激活名额
     */
    String ACTIVE_COUNT = "active_count";
    /**
     * 当前激活时间
     */
    String ACTIVE_TIME = "active_time";
    /**
     * 下次激活时间
     */
    String NEXT_ACTIVE_TIME = "next_active_time";
    /**
     * 抽奖库存key
     */
    String LOTTERY_DRAW = "lotteryDraw";
    /**
     * 中奖信息key
     */
    String LOTTERY_DRAW_INFO = "lottery_draw_info";
}
