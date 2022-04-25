package com.fanruan.platform.util;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

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
}
