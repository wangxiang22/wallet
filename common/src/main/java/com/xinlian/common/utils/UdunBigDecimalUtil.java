package com.xinlian.common.utils;

import com.xinlian.common.result.BizException;
import com.xinlian.common.result.ErrorInfoEnum;

import java.math.BigDecimal;
import java.text.DecimalFormat;

public class UdunBigDecimalUtil {

    public static final BigDecimal zeroBigDecimal = new BigDecimal("0");
    public static final BigDecimal minusZeroBigDecimal = new BigDecimal("-0");
    public static final BigDecimal maleChainUSDT = new BigDecimal("1000000");
    /**
     * 处理值小数点值
     * @param valueStr 待处理值
     * @param decimals 保留精度
     * @return
     */
    public static BigDecimal disposeValueDecimal(String valueStr,String decimals){
        return BigDecimal.valueOf(Double.valueOf(valueStr)).divide(new BigDecimal(10).pow(Integer.valueOf(decimals)));
    }

    /**
     * maleChain  - go
     * @param valueStr
     * @return
     */
    public static BigDecimal defaultDisposeValueDecimal(String valueStr){
        return disposeValueDecimal(valueStr,"6");
    }

    /**
     * 两个数相乘
     * @param decimalsValue1
     * @param decimalsValue2
     * @return
     */
    public static BigDecimal multiplyValueDecimal(BigDecimal decimalsValue1,BigDecimal decimalsValue2){
        return decimalsValue1.multiply(decimalsValue2);
    }

    public static BigDecimal divideValueDecimal(BigDecimal decimalsValue1,BigDecimal decimalsValue2){
        return decimalsValue1.divide(decimalsValue2, BigDecimal.ROUND_HALF_UP);
    }

    /**
     * 相加
     * @param b1
     * @param b2
     * @return
     */
    public static BigDecimal statisticAddNum(BigDecimal b1,BigDecimal b2){
        if(null==b1){ b1 = UdunBigDecimalUtil.zeroBigDecimal;}
        if(null==b2){ b2 = UdunBigDecimalUtil.zeroBigDecimal;}
        return b1.add(b2);
    }

    public static BigDecimal addNum(BigDecimal b1,BigDecimal b2){
        return b1.add(b2);
    }

    /**
     * 多个BigDecimal数相加和（多个BigDecimal已做初始化处理）
     * @param i
     * @param arg
     * @return
     */
    public static BigDecimal getBigDecimalSum(BigDecimal i, BigDecimal... arg) {
        BigDecimal sum = i;
        for (BigDecimal b : arg) {
            sum = sum.add(b);
        }
        return sum;
    }

    /**
     * 相减
     * @param b1
     * @param b2
     * @return
     */
    public static BigDecimal subtractNum(BigDecimal b1,BigDecimal b2){
        return b1.subtract(b2);
    }

    /**
     * b1-b2,并检验是否小于0
     * @param b1
     * @param b2
     * @return
     */
    public static BigDecimal subNumAndCheckIsZero(BigDecimal b1,BigDecimal b2){
        if(null==b2){ b2 = zeroBigDecimal;}
        BigDecimal result = b1.subtract(b2);
        if(result.compareTo(BigDecimal.ZERO)<0){
            throw new BizException(ErrorInfoEnum.NOT_SUFFICIENT_FUNDS);
        }
        return result;
    }

    /**
     * fee * percentage < minFee 返回最小值，反之亦然
     * @param fee
     * @param percentage
     * @param minFee
     * @return
     */
    public static BigDecimal getMinFee(BigDecimal fee,BigDecimal percentage,BigDecimal minFee){
        BigDecimal getResult = fee.multiply(percentage);
        if(getResult.compareTo(minFee)<0){
            return minFee;
        }else{
            return getResult;
        }
    }


    public static void main(String[] args) {
        String number = "1";
        System.err.println(judgeIfMinus(number));
    }

    public static BigDecimal convertMinusJudgeZero(BigDecimal num) {
        if(null==num || num.compareTo(zeroBigDecimal)==0){return UdunBigDecimalUtil.minusZeroBigDecimal;}
        if(num.compareTo(zeroBigDecimal)<0){return num;}
        return num.multiply(new BigDecimal("-1"));
    }

    public static BigDecimal convertMinus(String num) {
        return convertMinus(new BigDecimal(num));
    }

    public static BigDecimal convertMinus(BigDecimal bigDecimal) {
        if(bigDecimal.compareTo(zeroBigDecimal)<0){
            return bigDecimal;
        }
        return bigDecimal.multiply(new BigDecimal("-1"));
    }

    public static BigDecimal convertPlus(BigDecimal minusNum) {
        if(minusNum.compareTo(zeroBigDecimal)<0){
            return minusNum.multiply(new BigDecimal("-1"));
        }
        return minusNum;
    }

    public static BigDecimal convertBigDecimal(String num){
        return new BigDecimal(num);
    }

    /**
     * 判断是否为负值字符串
     * @return
     */
    public static boolean judgeIfMinus(String number){
        if (null == number || number.trim().isEmpty()) {
            return true;
        }
        BigDecimal convertNumber = convertBigDecimal(number);
        return convertNumber.compareTo(zeroBigDecimal)<=0;
    }
    public static boolean judgeIfMinus(BigDecimal number){
        if(null==number){return true;}
        return number.compareTo(zeroBigDecimal)<=0;
    }

    /**
     * 循环相减
     * @param i
     * @param bigDecimal
     * @return
     */
    public static BigDecimal arrayBigDecimalSub(BigDecimal i,BigDecimal ... bigDecimal) {
        BigDecimal difference = i;
        for (BigDecimal decimal : bigDecimal){
            difference = difference.subtract(decimal);
        }
        return difference;
    }

    /**
     * 循环相加
     * @param i
     * @param bigDecimal
     * @return
     */
    public static BigDecimal arrayBigDecimalAdd(BigDecimal i,BigDecimal ... bigDecimal) {
        BigDecimal difference = i;
        for (BigDecimal decimal : bigDecimal){
            difference = difference.add(decimal);
        }
        return difference;
    }

    public static String defaultFormatBigDecimal(BigDecimal number){
        DecimalFormat df1 = new DecimalFormat("###,##0.0000");
        return df1.format(number);
    }

    /**
     * 校验BigDecimal参数并保留四位数
     * @param bigDecimal 需校验的数值
     * @return 校验后返回的数值
     */
    public static BigDecimal checkBigDecimal(BigDecimal bigDecimal){
        if (null == bigDecimal) {
            bigDecimal = BigDecimal.ZERO;
        }else {
            bigDecimal = bigDecimal.setScale(4,BigDecimal.ROUND_DOWN);
        }
        return bigDecimal;
    }

    /**
     * 校验BigDecimal参数并保留八位数
     * @param bigDecimal 需校验的数值
     * @return 校验后返回的数值
     */
    public static BigDecimal checkBigDecimalEightDigit(BigDecimal bigDecimal){
        if (null == bigDecimal) {
            bigDecimal = BigDecimal.ZERO;
        }else {
            bigDecimal = bigDecimal.setScale(8,BigDecimal.ROUND_DOWN);
        }
        return bigDecimal;
    }

    /**
     * 校验BigDecimal参数，变为绝对值并保留四位数
     * @param bigDecimal 需校验的数值
     * @return 校验后返回的数值
     */
    public static BigDecimal checkAbsBigDecimal(BigDecimal bigDecimal){
        if (null == bigDecimal) {
            bigDecimal = BigDecimal.ZERO;
        }else {
            bigDecimal = bigDecimal.abs().setScale(4, BigDecimal.ROUND_DOWN);
        }
        return bigDecimal;
    }


}
