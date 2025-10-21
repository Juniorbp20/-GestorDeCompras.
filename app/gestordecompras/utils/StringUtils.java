package com.example.gestordecompras.utils;

import android.text.TextUtils;

public class StringUtils {

    /**
     * Capitaliza la primera letra de un string
     */
    public static String capitalizeFirstLetter(String text) {
        if (TextUtils.isEmpty(text)) return "";
        return text.substring(0, 1).toUpperCase() + text.substring(1).toLowerCase();
    }

    /**
     * Verifica si un string es un email válido
     */
    public static boolean isValidEmail(String email) {
        if (TextUtils.isEmpty(email)) return false;
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    /**
     * Verifica si un string es un teléfono válido
     */
    public static boolean isValidPhone(String phone) {
        if (TextUtils.isEmpty(phone)) return false;
        // Eliminar espacios, guiones, paréntesis
        String cleanPhone = phone.replaceAll("[\\s\\-()]", "");
        return cleanPhone.length() >= 8 && cleanPhone.matches("\\d+");
    }

    /**
     * Formatea un teléfono para mostrar
     */
    public static String formatPhoneNumber(String phone) {
        if (TextUtils.isEmpty(phone)) return "";

        String cleanPhone = phone.replaceAll("\\D", "");

        if (cleanPhone.length() == 10) {
            return String.format("(%s) %s-%s",
                    cleanPhone.substring(0, 3),
                    cleanPhone.substring(3, 6),
                    cleanPhone.substring(6));
        } else if (cleanPhone.length() == 7) {
            return String.format("%s-%s",
                    cleanPhone.substring(0, 3),
                    cleanPhone.substring(3));
        }

        return phone;
    }

    /**
     * Trunca un texto si es muy largo y agrega "..."
     */
    public static String truncateText(String text, int maxLength) {
        if (TextUtils.isEmpty(text)) return "";
        if (text.length() <= maxLength) return text;
        return text.substring(0, maxLength - 3) + "...";
    }

    /**
     * Verifica si un string contiene solo números
     */
    public static boolean isNumeric(String str) {
        if (TextUtils.isEmpty(str)) return false;
        return str.matches("\\d+");
    }

    /**
     * Limpia un string de caracteres especiales para búsquedas
     */
    public static String cleanForSearch(String text) {
        if (TextUtils.isEmpty(text)) return "";
        return text.trim().toLowerCase();
    }

    /**
     * Une un array de strings con un separador
     */
    public static String joinStrings(String[] strings, String separator) {
        if (strings == null || strings.length == 0) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        for (String string : strings) {
            if (!TextUtils.isEmpty(string)) {
                if (sb.length() > 0) {
                    sb.append(separator);
                }
                sb.append(string);
            }
        }
        return sb.toString();
    }
}