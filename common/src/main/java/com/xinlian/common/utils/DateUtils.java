package com.xinlian.common.utils;

import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.TimeZone;

public class DateUtils {

    public static String TIME_STYLE_S1 = "yyyy-MM-dd";
    public static String TIME_STYLE_S2 = "yyyy-MM-dd HH:mm";
    public static String TIME_STYLE_S3 = "yyyy-MM-dd HH:mm:ss";
    public static String TIME_STYLE_S4 = "yyyy-MM-dd HH:mm:ss:S";
    public static String TIME_STYLE_S5 = "yyyy-MM-dd HH:mm:ss:S E zZ";
    public static String TIME_STYLE_S6 = "yyyyMMddHHmmssS";
    public static String TIME_STYLE_S7 = "yyyy年MM月dd日HH时mm分ss秒";
    public static SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.S'Z'");

    static {
        DateUtils.SDF.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    public static String timeToString(Date time, final int style) {
        if (time == null) {
            time = new Date();
        }
        String timeStyle = null;
        switch (style) {
            case 1: {
                timeStyle = DateUtils.TIME_STYLE_S1;
                break;
            }
            case 2: {
                timeStyle = DateUtils.TIME_STYLE_S2;
                break;
            }
            case 3: {
                timeStyle = DateUtils.TIME_STYLE_S3;
                break;
            }
            case 4: {
                timeStyle = DateUtils.TIME_STYLE_S4;
                break;
            }
            case 5: {
                timeStyle = DateUtils.TIME_STYLE_S5;
                break;
            }
            case 6: {
                timeStyle = DateUtils.TIME_STYLE_S6;
                break;
            }
            case 7: {
                timeStyle = DateUtils.TIME_STYLE_S7;
                break;
            }
            case 8: {
                final SimpleDateFormat sdf = (SimpleDateFormat) DateUtils.SDF.clone();
                return sdf.format(time);
            }
            case 9: {
                return DateUtils.getEpochTime(time);
            }
            default: {
                return time.toString();
            }
        }
        return new SimpleDateFormat(timeStyle).format(time);
    }

    public static long getCurrentTime() {
        return System.currentTimeMillis() / 1000;
    }

    public static String timeToStringNull(final Date time, final int style) {
        return time == null ? null : DateUtils.timeToString(time, style);
    }

    public static String getUnixTime() {
        return Instant.now().toString();
    }

    public static String getEpochTime(final Date... time) {
        long milliseconds;
        if (time != null && time.length > 0) {
            milliseconds = time[0].getTime();
        } else {
            milliseconds = Instant.now().toEpochMilli();
        }
        milliseconds = milliseconds - 28800000L;
        final BigDecimal bd1 = new BigDecimal(milliseconds);
        final BigDecimal bd2 = new BigDecimal(1000);
        return bd1.divide(bd2).toString();
    }

    public static Date parseUTCTime(final String utcTime) throws ParseException {
        if (StringUtils.isEmpty(utcTime)) {
            return null;
        }
        final SimpleDateFormat sdfi = (SimpleDateFormat) DateUtils.SDF.clone();
        return sdfi.parse(utcTime);
    }

    public static Date parseDecimalTime(final String decimalTime) {
        if (StringUtils.isEmpty(decimalTime)) {
            return null;
        }
        final BigDecimal bd1 = new BigDecimal(decimalTime);
        final BigDecimal bd2 = new BigDecimal(1000);
        return new Date(bd1.multiply(bd2).longValue());
    }

    public static long getZeroTimes() {
        SimpleDateFormat sf = new SimpleDateFormat(TIME_STYLE_S1);
        Date today = null;
        try {
            today = sf.parse(sf.format(new Date()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return today.getTime();
    }


    public static long getStartDayTime(){
        SimpleDateFormat sf = new SimpleDateFormat(TIME_STYLE_S1);
        long time = 0;
        try {
            time = sf.parse(sf.format(new Date())).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return time;
    }

    public static long getEndDayTime(){
        SimpleDateFormat sf = new SimpleDateFormat(TIME_STYLE_S1);
        long time = 0;
        try {
            time = sf.parse(sf.format(new Date())).getTime() + 24 * 60 * 60 * 1000 - 1;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return time;
    }

    public static String timeFormatOne(Timestamp t) {
        return timeFormat(t, TIME_STYLE_S3);
    }

    public static String timeFormat(Timestamp t, String format) {
        if (t == null) {
            return "";
        }
        DateFormat df = new SimpleDateFormat(format);
        return df.format(t);
    }

}
