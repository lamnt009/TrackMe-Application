package com.example.trackme.utils;

import android.text.TextUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DateUtils {
    public static final String DATE_FORMAT_DD_MM_YYYY_HH_MM_SS = "ddMMYYYYhhmmss";
    public static final String DATE_FORMAT_HH_MM_SS = "HH:mm:ss";

    public static String formatDateToRecordId(Date date) {
        return convertDateToString(date, DATE_FORMAT_DD_MM_YYYY_HH_MM_SS);
    }

    public static String convertDateToString(Date date, String format) {
        if (date == null || TextUtils.isEmpty(format)) {
            return "";
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat(format, Locale.getDefault());
        return dateFormat.format(date);
    }

    public static String convertMinToHH(long second) {
        SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT_HH_MM_SS, Locale.getDefault());
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
        return format.format(new Date(second * 1000L));
    }
}
