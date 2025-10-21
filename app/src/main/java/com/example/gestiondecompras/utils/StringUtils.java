package com.example.gestiondecompras.utils;
import android.text.TextUtils;


public class StringUtils {
    public static String capitalizeFirstLetter(String text) {
        if (TextUtils.isEmpty(text)) return "";
        return text.substring(0, 1).toUpperCase() + text.substring(1).toLowerCase();
    }


    public static boolean isValidEmail(String email) {
        if (TextUtils.isEmpty(email)) return false;
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }


    public static boolean isValidPhone(String phone) {
        if (TextUtils.isEmpty(phone)) return false;
        String clean = phone.replaceAll("[\\s\\-\\(\\)]", "");
        return clean.length() >= 8 && clean.matches("\\d+");
    }


    public static String formatPhoneNumber(String phone) {
        if (TextUtils.isEmpty(phone)) return "";
        String clean = phone.replaceAll("[^\\d]", "");
        if (clean.length() == 10)
            return String.format("(%s) %s-%s", clean.substring(0,3), clean.substring(3,6), clean.substring(6));
        if (clean.length() == 7)
            return String.format("%s-%s", clean.substring(0,3), clean.substring(3));
        return phone;
    }


    public static String truncateText(String text, int maxLength) {
        if (TextUtils.isEmpty(text)) return "";
        if (text.length() <= maxLength) return text;
        return text.substring(0, Math.max(0, maxLength - 3)) + "...";
    }


    public static boolean isNumeric(String str) {
        if (TextUtils.isEmpty(str)) return false;
        return str.matches("\\d+");
    }


    public static String cleanForSearch(String text) {
        if (TextUtils.isEmpty(text)) return "";
        return text.trim().toLowerCase();
    }


    public static String joinStrings(String[] strings, String separator) {
        if (strings == null || strings.length == 0) return "";
        StringBuilder sb = new StringBuilder();
        for (String s : strings) {
            if (!TextUtils.isEmpty(s)) {
                if (sb.length() > 0) sb.append(separator);
                sb.append(s);
            }
        }
        return sb.toString();
    }
}