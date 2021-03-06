package com.fanruan.platform.util;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;

import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DateUtil {

    public static String transform(String dateStr){
//        String str = "1997-06-18T00:00:00.000Z";
        String str = dateStr.replace("Z"," UTC");
        Date date;
        try {
            date = DateUtils.parseDate(str, "yyyy-MM-dd'T'HH:mm:ss.SSS Z");
            return DateFormatUtils.format(date, "yyyy-MM-dd HH:mm:ss");
        } catch (ParseException e) {
            e.printStackTrace();
        }
     return null;
    }

    public static String formatDate(String dateStr){
        return dateStr.split("\\.")[0];
    }

    public static String transform2day(String dateStr){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date;
        try {
            date = sdf.parse(dateStr);
            return sdf.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String trans2StandardFormat(Date date){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        Date date;
        try {
//            date = sdf.parse(dateStr);
            return sdf.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static int getMonthDays(String yearMonth) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTime(sdf.parse(yearMonth));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        return calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
    }

    /**
     * ??????????????????
     * @param year
     * @param month
     * @return
     */
    public static String getLastDayOfMonth(int year,int month)
    {
        Calendar cal = Calendar.getInstance();
        //????????????
        cal.set(Calendar.YEAR,year);
        //????????????
        cal.set(Calendar.MONTH, month-1);
        //????????????????????????
        int lastDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        //????????????????????????????????????
        cal.set(Calendar.DAY_OF_MONTH, lastDay);
        //???????????????
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String lastDayOfMonth = sdf.format(cal.getTime());

        return lastDayOfMonth;
    }

    /**
     *  ???????????????????????????????????????
     * @param startTimeStr  ????????????
     * @param endTimeStr    ????????????
     * @return      ??????          ??????2018-11-01 00:00:00???2018-11-30 23:59:59  ?????????30
     */
    public static int getBetweenDays(String startTimeStr, String endTimeStr) {
        int betweenDays = 0;
        Date startTime = strToDateLong(startTimeStr);
        Date endTime = strToDateLong(endTimeStr);

        long start = startTime.getTime();
        long end = endTime.getTime();

        betweenDays = (int) (Math.abs(end - start)/(24*3600*1000));

        return betweenDays + 1;
    }

    public static Date strToDateLong(String strDate) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        ParsePosition pos = new ParsePosition(0);
        Date strtodate = formatter.parse(strDate, pos);
        return strtodate;
    }

    /**
     *  ??????????????????????????????????????? (?????????)
     *
     * @param startTime
     * @param endTime
     * @return
     */
    public static List<String> getBetweenDate(String startTime, String endTime){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        // ????????????????????????
        List<String> list = new ArrayList<String>();
        try {
            // ?????????????????????
            Date startDate = sdf.parse(startTime);
            Date endDate = sdf.parse(endTime);

            //???Calendar ????????????????????????
            Calendar calendar = Calendar.getInstance();
            while (startDate.getTime()<=endDate.getTime()){
                // ????????????????????????
                list.add(sdf.format(startDate));
                // ????????????
                calendar.setTime(startDate);
                //?????????????????????
                calendar.add(Calendar.DATE, 1);
                // ????????????????????????
                startDate=calendar.getTime();
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return list;
    }

}
