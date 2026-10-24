package com.loader.thuc_hanh_2_android.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class TimeUtils {

    public static final String DATE_FORMAT = "yyyy-MM-dd";
    public static final String TIME_FORMAT_FULL = "HH:mm:ss";
    public static final String TIME_FORMAT_SHORT = "HH:mm";

    private static SimpleDateFormat getDateFormat() {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT, Locale.US);
        sdf.setLenient(false);
        return sdf;
    }

    private static SimpleDateFormat getTimeFullFormat() {
        SimpleDateFormat sdf = new SimpleDateFormat(TIME_FORMAT_FULL, Locale.US);
        sdf.setLenient(false);
        return sdf;
    }

    private static SimpleDateFormat getTimeShortFormat() {
        SimpleDateFormat sdf = new SimpleDateFormat(TIME_FORMAT_SHORT, Locale.US);
        sdf.setLenient(false);
        return sdf;
    }

    
    public static String getCurrentDate() {
        return getDateFormat().format(new Date());
    }

    
    public static String getCurrentTime() {
        return getTimeFullFormat().format(new Date());
    }

    
    public static Date parseTime(String timeStr) throws ParseException {
        if (timeStr == null) {
            throw new ParseException("Thời gian không được để trống", 0);
        }
        String trimmed = timeStr.trim();
        if (trimmed.length() == 5) {
            return getTimeShortFormat().parse(trimmed);
        } else {
            return getTimeFullFormat().parse(trimmed);
        }
    }

    
    public static Date parseDate(String dateStr) throws ParseException {
        if (dateStr == null) {
            throw new ParseException("Ngày không được để trống", 0);
        }
        return getDateFormat().parse(dateStr.trim());
    }

    
    public static double roundToTwoDecimals(double value) {
        if (Double.isNaN(value) || Double.isInfinite(value)) {
            return 0.0;
        }
        return BigDecimal.valueOf(value)
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();
    }

    
    public static double calculateTotalHours(String checkInTime, String checkOutTime) {
        try {
            Date in = parseTime(checkInTime);
            Date out = parseTime(checkOutTime);

            long diffMillis = out.getTime() - in.getTime();
            if (diffMillis < 0) {
                return -1.0;
            }

            double hours = diffMillis / (1000.0 * 60.0 * 60.0);
            return roundToTwoDecimals(hours);
        } catch (Exception e) {
            return -1.0;
        }
    }

    
    public static double calculateDailyOvertime(double totalHours) {
        if (totalHours <= 8.0) {
            return 0.0;
        }
        return roundToTwoDecimals(totalHours - 8.0);
    }

    
    public static boolean isCheckOutAfterCheckIn(String checkInTime, String checkOutTime) {
        try {
            Date in = parseTime(checkInTime);
            Date out = parseTime(checkOutTime);
            return out.after(in);
        } catch (Exception e) {
            return false;
        }
    }

    
    public static boolean isFutureDate(String dateStr) {
        try {
            Date inputDate = parseDate(dateStr);

            Calendar calInput = Calendar.getInstance();
            calInput.setTime(inputDate);
            clearTimeFields(calInput);

            Calendar calToday = Calendar.getInstance();
            clearTimeFields(calToday);

            return calInput.after(calToday);
        } catch (Exception e) {
            return false;
        }
    }

    
    public static int getMonthFromDate(String dateStr) {
        try {
            Date date = parseDate(dateStr);
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            return cal.get(Calendar.MONTH) + 1;
        } catch (Exception e) {
            return -1;
        }
    }

    
    public static int getYearFromDate(String dateStr) {
        try {
            Date date = parseDate(dateStr);
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            return cal.get(Calendar.YEAR);
        } catch (Exception e) {
            return -1;
        }
    }

    private static void clearTimeFields(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
    }
}

