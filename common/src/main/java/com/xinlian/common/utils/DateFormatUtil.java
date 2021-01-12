package com.xinlian.common.utils;

import lombok.NonNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateFormatUtil {
    private static SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
    private static SimpleDateFormat sdf3 = new SimpleDateFormat("yyyyMMddHHmmss");
    private static SimpleDateFormat sdf4 = new SimpleDateFormat("yyyyMMdd");
    private static SimpleDateFormat sdf5 = new SimpleDateFormat("yyyy-MM-dd");
    public static String formatDateStyle = "yyyy-MM-dd HH:mm:ss";

    public static String formatTillSecond(Date date){
        if(null==date){return "";}
        return sdf1.format(date);
    }

    public static Date parseDateStr(String dateStr)throws Exception {
        if (null == dateStr || "".equals(dateStr)){ return null;}
        return sdf5.parse(dateStr);
    }

    public static Long dateToLong(Date date){
        return date == null ? null : date.getTime();
    }

    public static Date parseDateStr(@NonNull String dateStr, @NonNull String formatStr)throws Exception {
        SimpleDateFormat privateSdf = new SimpleDateFormat(formatStr);
        return privateSdf.parse(dateStr);
    }

    public static Long dateToLong(String dateStr,@NonNull String formatStr)throws Exception{
        Date getDate = parseDateStr(dateStr,formatStr);
        return getDate == null ? null : getDate.getTime();
    }

    /**
     * 返回系统当前的完整日期时间 <br>
     * 格式 1：2008-05-02 13:12:44 <br>
     * 格式 2：2008/05/02 13:12:44 <br>
     * 格式 3：2008年5月2日 13:12:44 <br>
     * 格式 4：2008年5月2日 13时12分44秒 <br>
     * 格式 5：2008年5月2日 星期五 13:12:44 <br>
     * 格式 6：2008年5月2日 星期五 13时12分44秒 <br>
     * 格式 7：20080502 <br>
     * 格式 8：20080502131244 <br>
     * 格式 9：2008-05-02 <br>
     * 格式 10：2008_05 <br>
     * 格式 11：2008 <br>
     * 格式 12：200805 <br>
     * 格式 13：2008-05 <br>
     * 格式 13：13 <br>
     * 格式 default：yyyyMMddHHmmss:20080502131244 <br>
     *
     * @param formatType (formatType) :格式代码号
     * @return 字符串
     */
    public static String get(int formatType, Date date) {
        SimpleDateFormat sdf = null;
        switch (formatType) {
            case 1:
                sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                break;
            case 2:
                sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                break;
            case 3:
                sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
                break;
            case 4:
                sdf = new SimpleDateFormat("yyyy年MM月dd日 HH时mm分ss秒");
                break;
            case 5:
                sdf = new SimpleDateFormat("yyyy年MM月dd日 E HH:mm:ss");
                break;
            case 6:
                sdf = new SimpleDateFormat("yyyy年MM月dd日 E HH时mm分ss秒");
                break;
            case 7:
                sdf = new SimpleDateFormat("yyyyMMdd");
                break;
            case 8:
                sdf = new SimpleDateFormat("yyyyMMddHHmmss");
                break;
            case 9:
                sdf = new SimpleDateFormat("yyyy-MM-dd");
                break;
            case 10:
                sdf = new SimpleDateFormat("yyyy_MM");
                break;
            case 11:
                sdf = new SimpleDateFormat("yyyy");
                break;
            case 12:
                sdf = new SimpleDateFormat("yyyyMM");
                break;
            case 13:
                sdf = new SimpleDateFormat("yyyy-MM");
                break;
            case 14:
                sdf = new SimpleDateFormat("yyyy年MM月dd日");
                break;
            case 15:
                sdf = new SimpleDateFormat("MM月dd日");
                break;
            case 16:
                sdf = new SimpleDateFormat("HH");
                break;
            default:
                sdf = new SimpleDateFormat("yyyyMMddHHmmss");
                break;
        }
        sdf.setLenient(false);
        return sdf.format(date);
    }

    public static String getByNowTime(int formatType){
        return get(formatType,new Date());
    }

    public static String printLogToGetNowTime(){
        return getByNowTime(7);
    }

    /**
     * 日期加天数
     * @param date 需要相加的时间
     * @param day 需要加的天数
     * @return 相加后的时间
     */
    public static Date addDate(Date date, int day) {
        Calendar rightNow = Calendar.getInstance();
        rightNow.setTime(date);
        rightNow.add(Calendar.DAY_OF_YEAR,day);
        return rightNow.getTime();
    }

    /**
     * 获取年龄，精确到天（周岁）
     * @param idCardNo 身份证号
     * @return 当前年龄（周岁）
     */
    public static Integer getAgeByIdCardNo(String idCardNo){
        //出生的年份
        Integer selectYear = Integer.valueOf(idCardNo.substring(6, 10));
        //出生的月份
        Integer selectMonth = Integer.valueOf(idCardNo.substring(10, 12));
        //出生的日期
        Integer selectDay = Integer.valueOf(idCardNo.substring(12, 14));
        Calendar cal = Calendar.getInstance();
        Integer yearMinus = cal.get(Calendar.YEAR) - selectYear;
        Integer monthMinus = cal.get(Calendar.MONTH) + 1 - selectMonth;
        Integer dayMinus = cal.get(Calendar.DATE) - selectDay;
        Integer age = yearMinus;
        if (yearMinus < 0) {
            age = 0;
        } else if (yearMinus == 0) {
            age = 0;
        } else {
            if (monthMinus == 0) {
                if (dayMinus < 0) {
                    age = age - 1;
                }
            }else if(monthMinus < 0){
                age = age - 1 ;
            }
        }
        return age;
    }

    /**
     * 固定格式的日期加一天（yyyyMMdd）
     * @param requestDate
     * @return
     * @throws java.text.ParseException
     */
    public static String getAddOneDate(String requestDate) throws ParseException {
        Date date = (new SimpleDateFormat("yyyyMMdd")).parse(requestDate);
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE,1);
        date =cal.getTime();
        return (new SimpleDateFormat("yyyyMMdd")).format(date);
    }

    /**
     * 固定格式的日期减三天（yyyy-MM-dd HH:mm:ss）
     * @param date
     * @return
     */
    public static String getSubThreeDate(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE,-3);
        date =cal.getTime();
        return (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(date);
    }

    public static String getSubOneDate(String requestDate) throws ParseException {
        Date date = (new SimpleDateFormat("yyyy-MM-dd")).parse(requestDate);
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE,-1);
        date =cal.getTime();
        return (new SimpleDateFormat("yyyy-MM-dd")).format(date);
    }

    public static void main(String[] args) throws Exception{
        System.err.println(getSubOneDate("2020-06-23"));
    }
}
