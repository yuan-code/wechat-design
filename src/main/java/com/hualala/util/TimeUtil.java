package com.hualala.util;

import org.joda.time.DateTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


/**
 * @author YuanChong
 * @create 2019-06-02 13:52
 * @desc
 */
public class TimeUtil {


    /**
     * 时间加上N个
     *
     * @param time yyyyMMddHHmmss
     * @param step
     * @return
     * @throws ParseException
     */
    public static Long stepTime(Long time,int calendarType, int step) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
            Date date = sdf.parse(time.toString());
            Calendar c = Calendar.getInstance();
            c.setTime(date);
            c.add(calendarType, step);
            return Long.valueOf(sdf.format(c.getTime()));
        }catch (Exception e) {
            throw new RuntimeException(time + "时间解析异常");
        }
    }

    /**
     * 返回当前时间的时间戳,格式yyyyMMdd
     *
     * @return
     */
    public static Long currentDT8() {
        DateTime now = DateTime.now();
        return currentDT8(now);

    }


    /**
     * 返回当前时间的时间戳,格式yyyyMMddHHmmss
     *
     * @return
     */
    public static Long currentDT() {
        DateTime now = DateTime.now();
        return currentDT(now);

    }

    /**
     * 时间戳转换yyyyMMddHHmmss
     *
     * @param timestamp
     * @return
     */
    public static Long covertTimestamp(Long timestamp) {
        Date date = new Date(timestamp);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        return Long.valueOf(sdf.format(date));
    }


    /**
     * 返回时间戳,格式yyyyMMdd
     *
     * @param time joda时间
     * @return
     */
    public static Long currentDT8(DateTime time) {
        return time.getYear() * 10000L +
                time.getMonthOfYear() * 100L +
                time.getDayOfMonth() * 1L;
    }

    /**
     * 返回时间戳,格式yyyyMMddHHmmss
     *
     * @param time joda时间
     * @return
     */
    public static Long currentDT(DateTime time) {
        return time.getYear() * 10000000000L +
                time.getMonthOfYear() * 100000000L +
                time.getDayOfMonth() * 1000000L +
                time.getHourOfDay() * 10000L +
                time.getMinuteOfHour() * 100L +
                time.getSecondOfMinute();
    }

    public static Long todayStartTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        Calendar todayStart = Calendar.getInstance();
        todayStart.set(Calendar.HOUR_OF_DAY, 0);
        todayStart.set(Calendar.MINUTE, 0);
        todayStart.set(Calendar.SECOND, 0);
        todayStart.set(Calendar.MILLISECOND, 0);
        return Long.valueOf(sdf.format(todayStart.getTime()));
    }

    public static Long todayEndTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        Calendar todayEnd = Calendar.getInstance();
        todayEnd.set(Calendar.HOUR_OF_DAY, 23);
        todayEnd.set(Calendar.MINUTE, 59);
        todayEnd.set(Calendar.SECOND, 59);
        todayEnd.set(Calendar.MILLISECOND, 999);
        return Long.valueOf(sdf.format(todayEnd.getTime()));
    }

}
