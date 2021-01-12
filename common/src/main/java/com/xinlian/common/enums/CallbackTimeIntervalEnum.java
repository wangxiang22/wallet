package com.xinlian.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 一般情况下，25小时以内完成8次通知（通知的间隔频率一般是：4m,10m,10m,1h,2h,6h,15h）；
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum CallbackTimeIntervalEnum {

    ONE(1, 4 * 60 * 1000L),
    TWO(2, 10 * 60 * 1000L),
    THREE(3, 10 * 60 * 1000L),
    FOUR(4, 60 * 60 * 1000L),
    FIVE(5, 2 * 60 * 60 * 1000L),
    SIX(6, 6 * 60 * 60 * 1000L),
    SEVEN(7, 15 * 60 * 60 * 1000L),;


    Integer count;
    Long time;

    public static Long getTimeByCount(Integer count) {
        switch (count) {
            case 1:
                return ONE.getTime();
            case 2:
                return TWO.getTime();
            case 3:
                return THREE.getTime();
            case 4:
                return FOUR.getTime();
            case 5:
                return FIVE.getTime();
            case 6:
                return SIX.getTime();
            default:
                return SEVEN.getTime();
        }
    }


}
