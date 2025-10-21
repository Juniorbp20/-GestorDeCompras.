package com.example.gestiondecompras.utils;

import android.text.TextUtils;

public class ValidationUtils {
    public static String validateCliente(String nombre, String telefono, String email) {
        if (TextUtils.isEmpty(nombre)) return "El nombre es obligatorio";
        if (nombre.length() < 2) return "El nombre debe tener al menos 2 caracteres";
        if (!TextUtils.isEmpty(telefono) && !StringUtils.isValidPhone(telefono)) return "El teléfono no es válido";
        if (!TextUtils.isEmpty(email) && !StringUtils.isValidEmail(email)) return "El email no es válido";
        return null; // ok
    }

    public static String validatePedido(double montoCompra, double ganancia, String cliente, String tienda) {
        if (TextUtils.isEmpty(cliente)) return "Selecciona un cliente";
        if (TextUtils.isEmpty(tienda)) return "Selecciona una tienda";
        if (montoCompra <= 0) return "El monto de compra debe ser mayor a 0";
        if (ganancia < 0) return "La ganancia no puede ser negativa";
        if (!CalculadoraGanancias.esMontoValido(montoCompra) || !CalculadoraGanancias.esMontoValido(ganancia)) return "Los montos no son válidos";
        return null; // ok
    }

    public static boolean isValidAmount(double amount) {
        return CalculadoraGanancias.esMontoValido(amount) && amount >= 0;
    }

    public static boolean isValidPercentage(double percentage) {
        return !Double.isNaN(percentage) && !Double.isInfinite(percentage) && percentage >= 0 && percentage <= 1000;
    }
}
