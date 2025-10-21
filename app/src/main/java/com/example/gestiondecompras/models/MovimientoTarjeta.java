package com.example.gestiondecompras.models;

import java.io.Serializable;
import java.util.Date;

public class MovimientoTarjeta implements Serializable {
    public static final String TIPO_GASTO = "GASTO"; // compra con tarjeta (aumenta deuda)
    public static final String TIPO_PAGO  = "PAGO";  // abono a la tarjeta (disminuye deuda)

    private int id;
    private int tarjetaId;
    private Integer pedidoId; // puede ser null si no viene de un pedido
    private Date fecha;
    private String tipo;      // GASTO | PAGO
    private double monto;     // monto positivo; el signo lo maneja la lógica de negocio
    private String descripcion;

    public MovimientoTarjeta() { this.fecha = new Date(); }
    public MovimientoTarjeta(int tarjetaId, Integer pedidoId, String tipo, double monto, String descripcion) {
        this(); this.tarjetaId=tarjetaId; this.pedidoId=pedidoId; this.tipo=tipo; this.monto=monto; this.descripcion=descripcion;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getTarjetaId() { return tarjetaId; }
    public void setTarjetaId(int tarjetaId) { this.tarjetaId = tarjetaId; }
    public Integer getPedidoId() { return pedidoId; }
    public void setPedidoId(Integer pedidoId) { this.pedidoId = pedidoId; }
    public Date getFecha() { return fecha; }
    public void setFecha(Date fecha) { this.fecha = fecha; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public double getMonto() { return monto; }
    public void setMonto(double monto) { this.monto = monto; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
}