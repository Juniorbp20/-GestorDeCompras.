package com.example.gestordecompras.utils;

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

    /**
     * Formatea una fecha para mostrar en la UI
     */
    public static String formatDateForDisplay(Date date) {
        if (date == null) return "Sin fecha";
        return DATE_FORMAT_DISPLAY.format(date);
    }

    /**
     * Formatea una fecha para la base de datos
     */
    public static String formatDateForDB(Date date) {
        if (date == null) return null;
        return DATE_FORMAT_DB.format(date);
    }

    /**
     * Formatea fecha y hora para mostrar
     */
    public static String formatDateTimeForDisplay(Date date) {
        if (date == null) return "Sin fecha";
        return DATE_TIME_FORMAT.format(date);
    }

    /**
     * Formatea mes y año (Ej: "Ene 2024")
     */
    public static String formatMonthYear(Date date) {
        if (date == null) return "";
        return MONTH_YEAR_FORMAT.format(date);
    }

    /**
     * Verifica si una fecha es hoy
     */
    public static boolean isToday(Date date) {
        if (date == null) return false;

        Calendar today = Calendar.getInstance();
        Calendar targetDate = Calendar.getInstance();
        targetDate.setTime(date);

        return today.get(Calendar.YEAR) == targetDate.get(Calendar.YEAR) &&
                today.get(Calendar.DAY_OF_YEAR) == targetDate.get(Calendar.DAY_OF_YEAR);
    }

    /**
     * Verifica si una fecha es mañana
     */
    public static boolean isTomorrow(Date date) {
        if (date == null) return false;

        Calendar tomorrow = Calendar.getInstance();
        tomorrow.add(Calendar.DAY_OF_YEAR, 1);

        Calendar targetDate = Calendar.getInstance();
        targetDate.setTime(date);

        return tomorrow.get(Calendar.YEAR) == targetDate.get(Calendar.YEAR) &&
                tomorrow.get(Calendar.DAY_OF_YEAR) == targetDate.get(Calendar.DAY_OF_YEAR);
    }

    /**
     * Verifica si una fecha está atrasada
     */
    public static boolean isPastDue(Date date) {
        if (date == null) return false;
        return date.before(new Date());
    }

    /**
     * Verifica si una fecha está en el futuro
     */
    public static boolean isFuture(Date date) {
        if (date == null) return false;
        return date.after(new Date());
    }

    /**
     * Obtiene la fecha de hoy
     */
    public static Date getToday() {
        return new Date();
    }

    /**
     * Obtiene la fecha de mañana
     */
    public static Date getTomorrow() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        return calendar.getTime();
    }

    /**
     * Agrega días a una fecha
     */
    public static Date addDays(Date date, int days) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_YEAR, days);
        return calendar.getTime();
    }

    /**
     * Diferencia en días entre dos fechas
     */
    public static int getDaysDifference(Date startDate, Date endDate) {
        if (startDate == null || endDate == null) return 0;

        long difference = endDate.getTime() - startDate.getTime();
        return (int) (difference / (1000 * 60 * 60 * 24));
    }

    /**
     * Convierte string a Date (para el formato de display)
     */
    public static Date parseDisplayDate(String dateString) {
        if (dateString == null || dateString.isEmpty()) return null;

        try {
            return DATE_FORMAT_DISPLAY.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Obtiene el inicio del día para una fecha
     */
    public static Date getStartOfDay(Date date) {
        if (date == null) return null;

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar.getTime();
    }

    /**
     * Obtiene el fin del día para una fecha
     */
    public static Date getEndOfDay(Date date) {
        if (date == null) return null;

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);

        return calendar.getTime();
    }

    /**
     * Verifica si dos fechas son el mismo día
     */
    public static boolean isSameDay(Date date1, Date date2) {
        if (date1 == null || date2 == null) return false;

        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(date1);
        cal2.setTime(date2);

        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
    }

    /**
     * Obtiene el nombre del día de la semana
     */
    public static String getDayName(Date date) {
        if (date == null) return "";

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        String[] days = {"Domingo", "Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado"};
        return days[calendar.get(Calendar.DAY_OF_WEEK) - 1];
    }

    /**
     * Obtiene una fecha a partir de día, mes y año
     */
    public static Date createDate(int day, int month, int year) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1, day); // Month es 0-based en Calendar
        return calendar.getTime();
    }
}