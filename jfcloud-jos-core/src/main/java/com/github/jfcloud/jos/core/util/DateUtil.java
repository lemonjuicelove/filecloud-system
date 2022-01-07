package com.github.jfcloud.jos.core.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author zj
 * @date 2021/12/31
 */
public class DateUtil {
    public DateUtil() {
    }

    public static String getCurrentTime() {
        Date date = new Date();
        String stringDate = String.format("%tF %<tT", date);
        return stringDate;
    }

    public static Date getDateByFormatString(String stringDate, String formatString) throws ParseException {
        DateFormat dateFormat = new SimpleDateFormat(formatString);
        Date date = dateFormat.parse(stringDate);
        return date;
    }

    public static int getDifferentDays(Date preDate, Date afterDate) {
        int preYear = getYear(preDate);
        int afterYear = getYear(afterDate);
        int preDayOfYear = getDayOfYear(preDate);
        int afterDayOfYear = getDayOfYear(afterDate);
        if (afterYear - preYear == 0) {
            return afterDayOfYear - preDayOfYear;
        } else {
            int diffDay;
            for(diffDay = 0; preYear < afterYear; ++preYear) {
                if (diffDay == 0 && isLeapYear(preYear)) {
                    diffDay = 366 - preDayOfYear;
                } else if (diffDay == 0 && !isLeapYear(preYear)) {
                    diffDay = 365 - preDayOfYear;
                } else if (isLeapYear(preYear)) {
                    diffDay += 366;
                } else {
                    diffDay += 365;
                }
            }

            diffDay += afterDayOfYear;
            return diffDay;
        }
    }

    public static int getDayOfYear(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int day = cal.get(6);
        return day;
    }

    public static int getYear(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int year = calendar.get(1);
        return year;
    }

    public static boolean isLeapYear(int year) {
        return year % 4 == 0 && year % 100 != 0 || year % 400 == 0;
    }

    public static long getTime() {
        long time = (new Date()).getTime();
        return time;
    }

    public static List<String> getRecent30DateList() {
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
        Date today = new Date();
        String date = fmt.format(today);
        String maxDateStr = date;
        String minDateStr = "";
        Calendar calc = Calendar.getInstance();
        ArrayList datefor30List = new ArrayList();

        try {
            for(int i = 0; i < 30; ++i) {
                calc.setTime(fmt.parse(maxDateStr));
                calc.add(5, -i);
                Date minDate = calc.getTime();
                minDateStr = fmt.format(minDate);
                datefor30List.add(minDateStr);
            }
        } catch (ParseException var9) {
            var9.printStackTrace();
        }

        Collections.reverse(datefor30List);
        return datefor30List;
    }
}
