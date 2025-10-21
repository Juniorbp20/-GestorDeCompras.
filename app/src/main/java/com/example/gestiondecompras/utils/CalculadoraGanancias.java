package com.example.gestiondecompras.utils;

import java.text.DecimalFormat;

public class CalculadoraGanancias {
    private static final DecimalFormat df = new DecimalFormat("#.##");

    /** Total = compra + ganancia */
    public static double calcularTotal(double montoCompra, double ganancia) {
        return redondear(montoCompra + ganancia);
    }

    /** Ganancia desde % del monto de compra */
    public static double calcularGananciaDesdePorcentaje(double montoCompra, double porcentaje) {
        if (montoCompra <= 0) return 0;
        return redondear(montoCompra * (porcentaje / 100.0));
    }

    /** % de ganancia en base a compra y ganancia */
    public static double calcularPorcentajeGanancia(double montoCompra, double ganancia) {
        if (montoCompra <= 0) return 0;
        return redondear((ganancia / montoCompra) * 100.0);
    }

    /** Ganancia neta restando costos adicionales */
    public static double calcularGananciaNeta(double gananciaBruta, double costosAdicionales) {
        return redondear(gananciaBruta - costosAdicionales);
    }

    /** Precio de venta con margen % */
    public static double calcularPrecioVenta(double costoProducto, double margenGananciaPorcentaje) {
        if (costoProducto <= 0) return 0;
        return redondear(costoProducto * (1 + (margenGananciaPorcentaje / 100.0)));
    }

    /** Valida monto */
    public static boolean esMontoValido(double monto) {
        return !Double.isNaN(monto) && !Double.isInfinite(monto) && monto >= 0;
    }

    /** Formatea monto RD$ */
    public static String formatearMonto(double monto) {
        if (!esMontoValido(monto)) return "RD$ 0.00";
        return String.format("RD$ %,.2f", monto);
    }

    /** Formatea porcentaje */
    public static String formatearPorcentaje(double porcentaje) {
        if (Double.isNaN(porcentaje) || Double.isInfinite(porcentaje)) return "0%";
        return String.format("%.1f%%", porcentaje);
    }

    /** Redondeo 2 decimales */
    public static double redondear(double valor) {
        if (Double.isNaN(valor) || Double.isInfinite(valor)) return 0.0;
        return Math.round(valor * 100.0) / 100.0;
    }

    /** Suma de montos */
    public static double sumarMontos(double... montos) {
        double total = 0;
        if (montos == null) return 0;
        for (double m : montos) if (esMontoValido(m)) total += m;
        return redondear(total);
    }

    /** Promedio de ganancia */
    public static double calcularPromedioGanancia(double totalGanancia, int numeroPedidos) {
        if (numeroPedidos <= 0) return 0;
        return redondear(totalGanancia / numeroPedidos);
    }
}
