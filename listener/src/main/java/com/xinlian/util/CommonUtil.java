package com.xinlian.util;

import com.sun.jmx.snmp.Timestamp;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>
 * 普通工具类
 * </p>
 * <pre> Created: 2019/08/11 22:09  </pre>
 *
 * @author lx
 * @version 1.0
 * @since JDK 1.8
 */
public class CommonUtil {
    private static final long ONE_MINUTE = 60000L;
    private static final long ONE_HOUR = 3600000L;
    private static final long ONE_DAY = 86400000L;
    private static final long ONE_WEEK = 604800000L;

    private static final String ONE_SECOND_AGO = "秒前";
    private static final String ONE_MINUTE_AGO = "分钟前";
    private static final String ONE_HOUR_AGO = "小时前";
    private static final String ONE_DAY_AGO = "天前";
    private static final String ONE_MONTH_AGO = "个月前";
    private static final String ONE_YEAR_AGO = "年前";

    public static String getTime(Date dateTime) throws ParseException {
        return format(dateTime);
    }

    /**
     * 将字符串转为金额串
     *
     * @param tradeTotalStr
     * @return
     */
    public static Double convetToNumber(String tradeTotalStr) {
        if (tradeTotalStr.contains("亿")) {
            tradeTotalStr = tradeTotalStr.replace("亿", "").replaceAll(",", "");
            return Double.parseDouble(tradeTotalStr) * 100000000;
        }
        if (tradeTotalStr.contains("万")) {
            tradeTotalStr = tradeTotalStr.replace("万", "").replaceAll(",", "");
            return Double.parseDouble(tradeTotalStr) * 10000;
        }
        tradeTotalStr = tradeTotalStr.replaceAll(",", "");
        return Double.parseDouble(tradeTotalStr);
    }

    /**
     * 获得转化后的金额
     *
     * @param money
     * @return
     */
    public static String getMoney(BigDecimal money) {
        if (money.doubleValue() < 10000) {
            return formatString(money.setScale(2, RoundingMode.HALF_UP).floatValue());
        } else if (money.doubleValue() < 100000000) {
            return formatString(money.divide(new BigDecimal(10000), 2, RoundingMode.HALF_UP)
                    .floatValue()).concat("万");
        } else {
            return formatString(money.divide(new BigDecimal(100000000), 2, RoundingMode.HALF_UP)
                    .floatValue()).concat("亿");
        }
    }

    public static String formatString(float data) {
        DecimalFormat df = new DecimalFormat("#,###.00");
        return df.format(data);
    }

    public static String formatString2(float data) {
        DecimalFormat df = new DecimalFormat("#,###");
        return df.format(data);
    }

    public static String format(Date date) {
        long delta = System.currentTimeMillis() - date.getTime();
        if (delta < 1L * ONE_MINUTE) {
            long seconds = toSeconds(delta);
            return (seconds <= 0 ? 1 : seconds) + ONE_SECOND_AGO;
        }
        if (delta < 45L * ONE_MINUTE) {
            long minutes = toMinutes(delta);
            return (minutes <= 0 ? 1 : minutes) + ONE_MINUTE_AGO;
        }
        if (delta < 24L * ONE_HOUR) {
            long hours = toHours(delta);
            return (hours <= 0 ? 1 : hours) + ONE_HOUR_AGO;
        }
        if (delta < 48L * ONE_HOUR) {
            return "昨天";
        }
        if (delta < 30L * ONE_DAY) {
            long days = toDays(delta);
            return (days <= 0 ? 1 : days) + ONE_DAY_AGO;
        }
        if (delta < 12L * 4L * ONE_WEEK) {
            long months = toMonths(delta);
            return (months <= 0 ? 1 : months) + ONE_MONTH_AGO;
        } else {
            long years = toYears(delta);
            return (years <= 0 ? 1 : years) + ONE_YEAR_AGO;
        }
    }

    private static long toSeconds(long date) {
        return date / 1000L;
    }

    private static long toMinutes(long date) {
        return toSeconds(date) / 60L;
    }

    private static long toHours(long date) {
        return toMinutes(date) / 60L;
    }

    private static long toDays(long date) {
        return toHours(date) / 24L;
    }

    private static long toMonths(long date) {
        return toDays(date) / 30L;
    }

    private static long toYears(long date) {
        return toMonths(date) / 365L;
    }

    /**
     * 生成随机6位验证码
     *
     * @return
     */
    public static String getSix() {
        Random rad = new Random();
        String result = rad.nextInt(1000000) + "";
        if (result.length() != 6) {
            return getSix();
        }
        return result;
    }

    public static Long getTimeForSeconds(Date date) {
        return date.getTime() / 1000;
    }

    /**
     * 获取时间戳,精确到天
     *
     * @return
     */
    public static Long getTimeStampForSeconds() {
        long dateStamp = System.currentTimeMillis() / 1000;
        String dateStampStr = String.valueOf(dateStamp);
        String dayTimeStamp = timeStamp2Date(dateStampStr, "yyyy-MM-dd");
        String data = date2TimeStamp(dayTimeStamp, "yyyy-MM-dd");
        return Long.parseLong(data);
    }

    /**
     * 获取今天0点时间戳
     */
    public static Long getTodayTimeStamp() {
        Long currentTimestamps = System.currentTimeMillis() / 1000;
        Long oneDayTimestamps = Long.valueOf(60 * 60 * 24);
        return currentTimestamps - (currentTimestamps + 60 * 60 * 8) % oneDayTimestamps;
    }

    /**
     * 时间戳转换日期
     *
     * @param seconds
     * @param format
     * @return
     */
    public static String timeStamp2Date(String seconds, String format) {
        if (seconds == null || seconds.isEmpty() || seconds.equals("null")) {
            return "";
        }
        if (format == null || format.isEmpty()) {
            format = "yyyy-MM-dd HH:mm:ss";
        }
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(new Date(Long.valueOf(seconds + "000")));
    }

    /**
     * 时间戳转换日期
     *
     * @param seconds
     * @param format
     * @return
     */
    public static String timeStamp2DateWithHandle(String seconds, String format) {
        if (seconds == null || seconds.isEmpty() || seconds.equals("null")) {
            return "";
        }
        if (format == null || format.isEmpty()) {
            format = "yyyy-MM-dd HH:mm:ss";
        }
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(new Date(Long.valueOf(seconds + "000")));
    }

    public static String millisecondTimeStampToFormatDate(String millisecondStr, String format) {
        if (millisecondStr == null || millisecondStr.isEmpty() || millisecondStr.equals("null")) {
            return "";
        }
        if (format == null || format.isEmpty()) {
            format = "yyyy-MM-dd HH:mm:ss";
        }
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(new Date(Long.valueOf(millisecondStr)));
    }

    /**
     * 日期格式字符串转换成时间戳
     *
     * @param date_str 字符串日期
     * @param format   如：yyyy-MM-dd HH:mm:ss
     * @return
     */
    public static String date2TimeStamp(String date_str, String format) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            return String.valueOf(sdf.parse(date_str).getTime() / 1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 取得当前时间戳（精确到秒）
     *
     * @return
     */
    public static Long timeStamp() {
        return System.currentTimeMillis() / 1_000;
    }

    /**
     * 日期转换时间戳
     *
     * @param date
     * @return
     */
    public static Long DateConvertTimeStamp(Date date) {
        return date.getTime() / 1000;
    }

    /**
     * 校验是否为空串
     *
     * @param validate
     * @return
     */
    public static boolean isEmpty(String validate) {
        if (validate == null || "".equals(validate.trim())) {
            return true;
        }
        return false;
    }

    /**
     * 校验手机格式
     *
     * @param phone
     * @return
     */
    public static boolean validatePhone(String phone) {
        String regExp = "^1[3|4|5|6|7|8][0-9]\\\\d{4,8}$";
        Pattern p = Pattern.compile(regExp);
        Matcher m = p.matcher(phone);
        return m.find();
    }

    /**
     * 校验邮箱格式
     *
     * @param email
     * @return
     */
    public static boolean validateEmail(String email) {
        String regExp = "^([a-z0-9A-Z]+[-|_|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
        Pattern p = Pattern.compile(regExp);
        Matcher m = p.matcher(email);
        return m.find();
    }

    public static double getYingkui(String variety, double currentValue, double enterValue, int flag, int isXingshu) {
        double result = 0;
        DecimalFormat df = new DecimalFormat("#.00");
        //浮动一位
        if ("XAUUSD".equals(variety) || "SPX500".equals(variety)) {
            if (flag == 0) { //买入
                result = Double.parseDouble(df.format(currentValue * 10 - enterValue * 10));
            } else {//卖出
                result = Double.parseDouble(df.format(enterValue * 10 - currentValue * 10));
            }
        } else if ("XAGUSD".equals(variety) || "XTIUSD".equals(variety) || "XNGUSD".equals(variety) || "USDJPY".equals(variety) || "GBPJPY".equals(variety) || "AUDJPY".equals(variety)) {
            if (flag == 0) { //买入
                if (isXingshu == 1) {
                    result = Double.parseDouble(df.format(currentValue * 10 - enterValue * 10));
                } else {
                    result = Double.parseDouble(df.format(currentValue * 100 - enterValue * 100));
                }
            } else {//卖出
                if (isXingshu == 1) {
                    result = Double.parseDouble(df.format(enterValue * 10 - currentValue * 10));
                } else {
                    result = Double.parseDouble(df.format(enterValue * 100 - currentValue * 100));
                }
            }
        } else if ("COPPER".equals(variety)) {
            if (flag == 0) { //买入
                if (isXingshu == 1) {
                    result = Double.parseDouble(df.format(currentValue * 100 - enterValue * 100));
                } else {
                    result = Double.parseDouble(df.format(currentValue * 1000 - enterValue * 1000));
                }
            } else {//卖出
                if (isXingshu == 1) {
                    result = Double.parseDouble(df.format(enterValue * 100 - currentValue * 100));
                } else {
                    result = Double.parseDouble(df.format(enterValue * 1000 - currentValue * 1000));
                }
            }
        } else if ("EURUSD".equals(variety) || "GBPUSD".equals(variety) || "USDCHF".equals(variety) || "USDCAD".equals(variety) || "NZDUSD".equals(variety) || "AUDCAD".equals(variety) || "EURCAD".equals(variety) || "GBPAUD".equals(variety) || "AUDUSD".equals(variety)) {
            if (flag == 0) { //买入
                result = Double.parseDouble(df.format(currentValue * 10000 - enterValue * 10000));
            } else {//卖出
                result = Double.parseDouble(df.format(enterValue * 10000 - currentValue * 10000));
            }
        }

        return result;
    }

    public static Long exchangeTime(long l) {
        Long b = l;
        if (b != 0) {
            String substring = b.toString().substring(0, 10);
            Long time = Long.parseLong(substring);
            return time;
        }
        return 0L;
    }

    public static void main(String[] args) throws Exception {
        Long time = System.currentTimeMillis();  //当前时间的时间戳
        long zero = time / (1000 * 3600 * 24) * (1000 * 3600 * 24) - TimeZone.getDefault().getRawOffset();
        System.out.println(new Timestamp(zero));//今天零点零分零秒
        System.out.println(zero);

        Calendar calendar = Calendar.getInstance();
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), 23, 59, 59);
        long tt = calendar.getTime().getTime();
        System.out.println(tt);
//        System.err.println(getCurrentDateStr());
    }

    //当日0点 -- 13位时间戳
    public static Long getTodayStartTime() {
        Long time = System.currentTimeMillis();  //当前时间的时间戳
        long zero = time / (1000 * 3600 * 24) * (1000 * 3600 * 24) - TimeZone.getDefault().getRawOffset();
        return zero;
    }

    //当日23：59：59 -- 13位时间戳
    public static Long getTodayEndTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), 23, 59, 59);
        long tt = calendar.getTime().getTime();
        return tt;
    }

    public static String getRandomString(int stringLength) {
        String string = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < stringLength; i++) {
            int index = (int) Math.floor(Math.random() * string.length());//向下取整0-25
            sb.append(string.charAt(index));
        }
        return sb.toString();
    }

    public static String getRandomNub(int stringLength) {
        String string = "0123456789";
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < stringLength; i++) {
            int index = (int) Math.floor(Math.random() * string.length());
            sb.append(string.charAt(index));
        }
        return sb.toString();
    }

    /**
     * 获取当前时间到24点还剩下多少秒
     *
     * @return
     */
    public static long getTheDayResidueSecond() {
        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE), 23, 59, 59);
        calendar.set(Calendar.MILLISECOND, 0);
        long delta = calendar.getTime().getTime() - date.getTime();
        return toSeconds(delta);
    }

    /**
     * 当前月的最后一天数值
     *
     * @return
     */
    public static int getCurrentMonthLastDay() {
        SimpleDateFormat format = new SimpleDateFormat("dd");
        Calendar ca = Calendar.getInstance();
        ca.set(Calendar.DAY_OF_MONTH, ca.getActualMaximum(Calendar.DAY_OF_MONTH));
        String last = format.format(ca.getTime());
        return Integer.parseInt(last);
    }

    /**
     * 符合中国习惯的周一 - 所在日期
     *
     * @param date
     * @return
     */
    public static String getFirstDayOfWeek(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.DAY_OF_WEEK, 1);
        cal.set(Calendar.DATE, cal.get(Calendar.DATE) + 1);
        return format.format(cal.getTime()) + " 00:00:00";
    }

    /**
     * 符合中国习惯的周日 - 所在日期
     *
     * @param date
     * @return
     */
    public static String getLastDayOfWeek(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.DAY_OF_WEEK, 1);
        cal.set(Calendar.DATE, cal.get(Calendar.DATE) + 7);
        return format.format(cal.getTime()) + " 23:59:59";
    }

    public static String getCurrentDateStr() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        return format.format(cal.getTime());
    }

    public static String getIPAddress(HttpServletRequest request) {
        String ip = null;
        //X-Forwarded-For：Squid 服务代理
        String ipAddresses = request.getHeader("X-Forwarded-For");
        if (ipAddresses == null || ipAddresses.length() == 0 || "unknown".equalsIgnoreCase(ipAddresses)) {
            //Proxy-Client-IP：apache 服务代理
            ipAddresses = request.getHeader("Proxy-Client-IP");
        }
        if (ipAddresses == null || ipAddresses.length() == 0 || "unknown".equalsIgnoreCase(ipAddresses)) {
            //WL-Proxy-Client-IP：weblogic 服务代理
            ipAddresses = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ipAddresses == null || ipAddresses.length() == 0 || "unknown".equalsIgnoreCase(ipAddresses)) {
            //HTTP_CLIENT_IP：有些代理服务器
            ipAddresses = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ipAddresses == null || ipAddresses.length() == 0 || "unknown".equalsIgnoreCase(ipAddresses)) {
            //X-Real-IP：nginx服务代理
            ipAddresses = request.getHeader("X-Real-IP");
        }

        //有些网络通过多层代理，那么获取到的ip就会有多个，一般都是通过逗号（,）分割开来，并且第一个ip为客户端的真实IP
        if (ipAddresses != null && ipAddresses.length() != 0) {
            ip = ipAddresses.split(",")[0];
        }

        //还是不能获取到，最后再通过request.getRemoteAddr();获取
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ipAddresses)) {
            ip = request.getRemoteAddr();
        }
        return ip.equals("0:0:0:0:0:0:0:1") ? "127.0.0.1" : ip;
    }

    public static String getDevice(HttpServletRequest request) {
        String header = request.getHeader("user-agent");
        return header;
    }
}

