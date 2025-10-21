package com.example.gestordecompras.models;

import java.io.Serializable;
import java.util.Date;

public class Pedido implements Serializable {
    private int id;
    private int clienteId;
    private String clienteNombre; // Para mostrar sin hacer join
    private String tienda;
    private double montoCompra;
    private double ganancia;
    private double totalGeneral;
    private Date fechaEntrega;
    private Date fechaCreacion;
    private String estado; // PENDIENTE, ENTREGADO, PAGADO, CANCELADO
    private String notas;

    // Constantes para estados
    public static final String ESTADO_PENDIENTE = "PENDIENTE";
    public static final String ESTADO_ENTREGADO = "ENTREGADO";
    public static final String ESTADO_PAGADO = "PAGADO";
    public static final String ESTADO_CANCELADO = "CANCELADO";

    // Constructores
    public Pedido() {
        this.fechaCreacion = new Date();
        this.estado = ESTADO_PENDIENTE;
    }

    public Pedido(int clienteId, String clienteNombre, String tienda, double montoCompra,
                  double ganancia, Date fechaEntrega) {
        this();
        this.clienteId = clienteId;
        this.clienteNombre = clienteNombre;
        this.tienda = tienda;
        this.montoCompra = montoCompra;
        this.ganancia = ganancia;
        this.totalGeneral = montoCompra + ganancia;
        this.fechaEntrega = fechaEntrega;
    }

    // Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getClienteId() {
        return clienteId;
    }

    public void setClienteId(int clienteId) {
        this.clienteId = clienteId;
    }

    public String getClienteNombre() {
        return clienteNombre;
    }

    public void setClienteNombre(String clienteNombre) {
        this.clienteNombre = clienteNombre;
    }

    public String getTienda() {
        return tienda;
    }

    public void setTienda(String tienda) {
        this.tienda = tienda;
    }

    public double getMontoCompra() {
        return montoCompra;
    }

    public void setMontoCompra(double montoCompra) {
        this.montoCompra = montoCompra;
        calcularTotalGeneral();
    }

    public double getGanancia() {
        return ganancia;
    }

    public void setGanancia(double ganancia) {
        this.ganancia = ganancia;
        calcularTotalGeneral();
    }

    public double getTotalGeneral() {
        return totalGeneral;
    }

    public void setTotalGeneral(double totalGeneral) {
        this.totalGeneral = totalGeneral;
    }

    public Date getFechaEntrega() {
        return fechaEntrega;
    }

    public void setFechaEntrega(Date fechaEntrega) {
        this.fechaEntrega = fechaEntrega;
    }

    public Date getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(Date fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getNotas() {
        return notas;
    }

    public void setNotas(String notas) {
        this.notas = notas;
    }

    // Métodos de negocio
    private void calcularTotalGeneral() {
        this.totalGeneral = this.montoCompra + this.ganancia;
    }

    public double calcularPorcentajeGanancia() {
        if (montoCompra == 0) return 0;
        return (ganancia / montoCompra) * 100;
    }

    public boolean estaAtrasado() {
        if (fechaEntrega == null || !estado.equals(ESTADO_PENDIENTE)) {
            return false;
        }
        return fechaEntrega.before(new Date());
    }

    public boolean puedeMarcarComoEntregado() {
        return estado.equals(ESTADO_PENDIENTE);
    }

    public boolean puedeMarcarComoPagado() {
        return estado.equals(ESTADO_ENTREGADO);
    }

    @Override
    public String toString() {
        return "Pedido{" +
                "cliente='" + clienteNombre + '\'' +
                ", tienda='" + tienda + '\'' +
                ", total=" + totalGeneral +
                ", estado='" + estado + '\'' +
                '}';
    }
}