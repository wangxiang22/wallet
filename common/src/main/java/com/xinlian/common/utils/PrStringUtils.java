package com.xinlian.common.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Map;

/**
 * com.xinlian.common.utils
 *
 * @date 2020/2/13 11:27
 */
@Slf4j
public class PrStringUtils {


    /**
     * 手机号脱敏
     * @param mobile
     */
    public static String mobileTuoMin(String mobile){
        if(StringUtils.isEmpty(mobile)){
            return "";
        }
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(mobile.substring(0,3)).append("****").append(mobile.substring(7));
        return stringBuffer.toString();
    }

    /**
     * 银行脱敏
     * @param accNo 银行卡号
     * @return
     */
    public static String accNoTuoMin(String accNo){
        if(StringUtils.isEmpty(accNo)){
            return "";
        }
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(accNo.substring(0,4));
        int tuoMiLength = accNo.length()-4;
        for(int i=0;i<8;i++){
            stringBuffer.append("*");
        }
        stringBuffer.append(accNo.substring(tuoMiLength));
        return stringBuffer.toString();
    }

    public static String idNoTuoMin(String idNo){
        if(StringUtils.isEmpty(idNo)){
            return "";
        }
        StringBuffer stringBuffer = new StringBuffer();
        int len = idNo.length();
        stringBuffer.append(idNo.substring(0,6)).append("********").append(idNo.substring(len-3,len));
        return stringBuffer.toString();
    }

    public static String userNameTuoMin(String userName){
        if(StringUtils.isEmpty(userName)){
            return "";
        }
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("*");
        stringBuffer.append(userName.substring(1,userName.length()));
        return stringBuffer.toString();
    }

    public static String emailTuoMin(String email){
        if(StringUtils.isEmpty(email)){
            return "";
        }
        return email.replaceAll("(\\w+)\\w{0}@(\\w+)", "$1***@$2");
    }

    public static String pensionCardNoTuoMin(String pensionCardNo){
        if(StringUtils.isEmpty(pensionCardNo)){
            return "";
        }
        StringBuffer stringBuffer = new StringBuffer();
        int len = pensionCardNo.length();
        stringBuffer.append(pensionCardNo.substring(0,3)).append("****").append(pensionCardNo.substring(len-4,len));
        return stringBuffer.toString();
    }

    public static String fmtNumToString(int text) {
        DecimalFormat df =  new DecimalFormat("###,##0");
        double number = 0.0D;
        try {
            number = Double.parseDouble(text+"");
        } catch (Exception var6) {
            log.info("PrStringUtils--- fmtMicrometer(),出现异常",var6);
            number = 0D;
        }
        return df.format(number);
    }

    public static String fmtNumToString(Long text) {
        if(null==text){return "0";}
        DecimalFormat df =  new DecimalFormat("###,##0");
        double number = 0.0D;
        try {
            number = Double.parseDouble(text+"");
        } catch (Exception var6) {
            log.info("PrStringUtils--- fmtMicrometer(),出现异常",var6);
            number = 0D;
        }
        return df.format(number);
    }

    public static String fmtNumToString(BigDecimal text) {
        if(null==text){return "0";}
        return fmtNumToString(text.longValue());
    }

    public static String fmtNumToString(BigDecimal text,String defValue) {
        if(null==text){return defValue;}
        return fmtNumToString(text.longValue());
    }

    public static void main(String[] args) {
        BigDecimal de = new BigDecimal("0");
        System.out.println(fmtNumToString(de,"0"));
    }

    public static void convertTo(Map<String,Object> searchParam, String dateKey){
        String startDate = searchParam.get(dateKey+"StartDate")==null?null:searchParam.get(dateKey+"StartDate").toString()+" 00:00:00";
        String endDate = searchParam.get(dateKey+"EndDate")==null?null:searchParam.get(dateKey+"EndDate").toString()+" 23:59:59";
        if(startDate!=null) {
            searchParam.put(dateKey + "StartDate", startDate);
        }else{
            searchParam.put(dateKey + "StartDate", CommonUtil.getCurrentDateStr()+" 00:00:00");
        }
        if(endDate!=null){
            searchParam.put(dateKey+"EndDate",endDate);
        }else {
            searchParam.put(dateKey+"EndDate",CommonUtil.getCurrentDateStr()+" 23:59:59");
        }
    }

}
