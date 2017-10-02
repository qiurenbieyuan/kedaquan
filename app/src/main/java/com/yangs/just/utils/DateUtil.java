package com.yangs.just.utils;

import android.util.Log;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by yangs on 2017/9/18 0018.
 */

public class DateUtil {

    /**
     * 取得当前日期的星期
     */
    public static int getDayofWeek(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        calendar.setTime(date);
        int dayForWeek;
        if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY) {
            dayForWeek = 7;
        } else {
            dayForWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        }
        return dayForWeek;
    }

    /**
     * 取得当前日期所在周的第一天
     */
    public static Date getFirstDayOfWeek(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_WEEK,
                calendar.getFirstDayOfWeek()); // MONDAY
        return calendar.getTime();
    }

    /**
     * 得到day天后的Date
     */
    public static Date getDateAfter(Date d, int day) {
        Calendar now = Calendar.getInstance();
        now.setTime(d);
        now.set(Calendar.DATE, now.get(Calendar.DATE) + day);
        return now.getTime();
    }

    /**
     * 得到day天后的Day
     */
    public static int geDayAfter(Date d, int day) {
        Calendar now = Calendar.getInstance();
        now.setTime(d);
        now.set(Calendar.DATE, now.get(Calendar.DATE) + day);
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(now.getTime().getTime());
        return cal.get(Calendar.DATE);
    }
}
