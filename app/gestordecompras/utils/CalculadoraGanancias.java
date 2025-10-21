package com.example.gestordecompras.utils;

import java.text.DecimalFormat;

public class CalculadoraGanancias {

    private static final DecimalFormat df = new DecimalFormat("#.##");

    /**
     * Calcula el total general sumando monto de compra + ganancia
     */
    public static double calcularTotal(double montoCompra, double ganancia) {
        return redondear(montoCompra + ganancia);
    }

    /**
     * Calcula la ganancia basada en un porcentaje del monto de compra
     */
    public static double calcularGananciaDesdePorcentaje(double montoCompra, double porcentaje) {
        if (montoCompra <= 0) return 0;
        double ganancia = montoCompra * (porcentaje / 100);
        return redondear(ganancia);
    }

    /**
     * Calcula el porcentaje de ganancia basado en monto de compra y ganancia
     */
    public static double calcularPorcentajeGanancia(double montoCompra, double ganancia) {
        if (montoCompra <= 0) return 0;
        double porcentaje = (ganancia / montoCompra) * 100;
        return redondear(porcentaje);
    }

    /**
     * Calcula la ganancia neta restando costos adicionales
     */
    public static double calcularGananciaNeta(double gananciaBruta, double costosAdicionales) {
        return redondear(gananciaBruta - costosAdicionales);
    }

    /**
     * Calcula el precio de venta sugerido con un margen de ganancia específico
     */
    public static double calcularPrecioVenta(double costoProducto, double margenGananciaPorcentaje) {
        if (costoProducto <= 0) return 0;
        double precioVenta = costoProducto * (1 + (margenGananciaPorcentaje / 100));
        return redondear(precioVenta);
    }

    /**
     * Valida si un monto es válido para operaciones
     */
    public static boolean esMontoValido(double monto) {
        return !Double.isNaN(monto) && !Double.isInfinite(monto) && monto >= 0;
    }

    /**
     * Formatea un monto a string con formato de dinero
     */
    public static String formatearMonto(double monto) {
        if (!esMontoValido(monto)) {
            return "RD$ 0.00";
        }
        return String.format("RD$ %,.2f", monto);
    }

    /**
     * Formatea un porcentaje a string
     */
    public static String formatearPorcentaje(double porcentaje) {
        if (Double.isNaN(porcentaje) || Double.isInfinite(porcentaje)) {
            return "0%";
        }
        return String.format("%.1f%%", porcentaje);
    }

    /**
     * Redondea un número a 2 decimales
     */
    public static double redondear(double valor) {
        if (Double.isNaN(valor) || Double.isInfinite(valor)) {
            return 0.0;
        }
        return Math.round(valor * 100.0) / 100.0;
    }

    /**
     * Calcula el total de una lista de montos
     */
    public static double sumarMontos(double... montos) {
        double total = 0;
        for (double monto : montos) {
            if (esMontoValido(monto)) {
                total += monto;
            }
        }
        return redondear(total);
    }

    /**
     * Calcula el promedio de ganancia
     */
    public static double calcularPromedioGanancia(double totalGanancia, int numeroPedidos) {
        if (numeroPedidos <= 0) return 0;
        return redondear(totalGanancia / numeroPedidos);
    }
}