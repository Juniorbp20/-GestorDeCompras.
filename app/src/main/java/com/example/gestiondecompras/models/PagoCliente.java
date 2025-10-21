package com.example.gestiondecompras.models;

import java.io.Serializable;
import java.util.Date;

public class PagoCliente implements Serializable {
    public static final String METODO_EFECTIVO = "EFECTIVO";
    public static final String METODO_TRANSFERENCIA = "TRANSFERENCIA";

    private int id;
    private int pedidoId;
    private Date fecha;
    private double monto;
    private String metodo;
    private String referencia;

    public PagoCliente() { this.fecha = new Date(); }
    public PagoCliente(int pedidoId, double monto, String metodo, String referencia) {
        this(); this.pedidoId=pedidoId; this.monto=monto; this.metodo=metodo; this.referencia=referencia;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getPedidoId() { return pedidoId; }
    public void setPedidoId(int pedidoId) { this.pedidoId = pedidoId; }
    public Date getFecha() { return fecha; }
    public void setFecha(Date fecha) { this.fecha = fecha; }
    public double getMonto() { return monto; }
    public void setMonto(double monto) { this.monto = monto; }
    public String getMetodo() { return metodo; }
    public void setMetodo(String metodo) { this.metodo = metodo; }
    public String getReferencia() { return referencia; }
    public void setReferencia(String referencia) { this.referencia = referencia; }
}