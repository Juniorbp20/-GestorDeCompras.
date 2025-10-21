package com.example.gestiondecompras.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateUtils {
    private static final SimpleDateFormat DATE_FORMAT_DISPLAY = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    private static final SimpleDateFormat DATE_FORMAT_DB = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private static final SimpleDateFormat DATE_TIME_FORMAT = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
    private static final SimpleDateFormat MONTH_YEAR_FORMAT = new SimpleDateFormat("MMM yyyy", Locale.getDefault());

    public static String formatDateForDisplay(Date date) {
        if (date == null) return "Sin fecha";
        return DATE_FORMAT_DISPLAY.format(date);
    }

    public static String formatDateForDB(Date date) {
        if (date == null) return null;
        return DATE_FORMAT_DB.format(date);
    }

    public static String formatDateTimeForDisplay(Date date) {
        if (date == null) return "Sin fecha";
        return DATE_TIME_FORMAT.format(date);
    }

    public static String formatMonthYear(Date date) {
        if (date == null) return "";
        return MONTH_YEAR_FORMAT.format(date);
    }

    public static boolean isToday(Date date) {
        if (date == null) return false;
        Calendar today = Calendar.getInstance();
        Calendar target = Calendar.getInstance();
        target.setTime(date);
        return today.get(Calendar.YEAR) == target.get(Calendar.YEAR)
                && today.get(Calendar.DAY_OF_YEAR) == target.get(Calendar.DAY_OF_YEAR);
    }

    public static boolean isTomorrow(Date date) {
        if (date == null) return false;
        Calendar tomorrow = Calendar.getInstance();
        tomorrow.add(Calendar.DAY_OF_YEAR, 1);
        Calendar target = Calendar.getInstance();
        target.setTime(date);
        return tomorrow.get(Calendar.YEAR) == target.get(Calendar.YEAR)
                && tomorrow.get(Calendar.DAY_OF_YEAR) == target.get(Calendar.DAY_OF_YEAR);
    }

    public static boolean isPastDue(Date date) {
        if (date == null) return false;
        return date.before(new Date());
    }

    public static boolean isFuture(Date date) {
        if (date == null) return false;
        return date.after(new Date());
    }

    public static Date getToday() { return new Date(); }

    public static Date getTomorrow() {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_YEAR, 1);
        return c.getTime();
    }

    public static Date addDays(Date date, int days) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DAY_OF_YEAR, days);
        return c.getTime();
    }

    public static int getDaysDifference(Date startDate, Date endDate) {
        if (startDate == null || endDate == null) return 0;
        long diff = endDate.getTime() - startDate.getTime();
        return (int) (diff / (1000 * 60 * 60 * 24));
    }

    public static Date parseDisplayDate(String dateString) {
        if (dateString == null || dateString.isEmpty()) return null;
        try {
            return DATE_FORMAT_DISPLAY.parse(dateString);
        } catch (ParseException e) {
            return null;
        }
    }

    public static Date getStartOfDay(Date date) {
        if (date == null) return null;
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTime();
    }

    public static Date getEndOfDay(Date date) {
        if (date == null) return null;
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(Calendar.HOUR_OF_DAY, 23);
        c.set(Calendar.MINUTE, 59);
        c.set(Calendar.SECOND, 59);
        c.set(Calendar.MILLISECOND, 999);
        return c.getTime();
    }

    public static boolean isSameDay(Date date1, Date date2) {
        if (date1 == null || date2 == null) return false;
        Calendar a = Calendar.getInstance();
        Calendar b = Calendar.getInstance();
        a.setTime(date1); b.setTime(date2);
        return a.get(Calendar.YEAR) == b.get(Calendar.YEAR)
                && a.get(Calendar.DAY_OF_YEAR) == b.get(Calendar.DAY_OF_YEAR);
    }

    public static String getDayName(Date date) {
        if (date == null) return "";
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        String[] days = {"Domingo", "Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado"};
        return days[calendar.get(Calendar.DAY_OF_WEEK) - 1];
    }

    public static Date createDate(int day, int month, int year) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1, day); // month 0-based
        return calendar.getTime();
    }
}