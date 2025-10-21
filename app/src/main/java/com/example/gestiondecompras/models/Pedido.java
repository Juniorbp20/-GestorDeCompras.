package com.example.gestiondecompras.models;
import java.io.Serializable;
import java.util.Date;


public class Pedido implements Serializable {
    private int id;
    private int clienteId;
    private String clienteNombre; // duplicado para mostrar sin JOIN
    private String tienda;
    private double montoCompra;
    private double ganancia;
    private double totalGeneral;
    private Date fechaEntrega;
    private Date fechaCreacion;
    private String estado; // PENDIENTE, ENTREGADO, PAGADO, CANCELADO
    private String notas;
    private Integer tarjetaId;
    private String tarjetaAlias;


    public static final String ESTADO_PENDIENTE = "PENDIENTE";
    public static final String ESTADO_ENTREGADO = "ENTREGADO";
    public static final String ESTADO_PAGADO = "PAGADO";
    public static final String ESTADO_CANCELADO = "CANCELADO";


    public Pedido() {
        this.fechaCreacion = new Date();
        this.estado = ESTADO_PENDIENTE;
        this.tarjetaId = null;
        this.tarjetaAlias = null;
    }
    public Pedido(int clienteId, String clienteNombre, String tienda, double montoCompra, double ganancia, Date fechaEntrega) {
        this();
        this.clienteId = clienteId;
        this.clienteNombre = clienteNombre;
        this.tienda = tienda;
        this.montoCompra = montoCompra;
        this.ganancia = ganancia;
        this.totalGeneral = montoCompra + ganancia;
        this.fechaEntrega = fechaEntrega;
    }


    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getClienteId() { return clienteId; }
    public void setClienteId(int clienteId) { this.clienteId = clienteId; }
    public String getClienteNombre() { return clienteNombre; }
    public void setClienteNombre(String clienteNombre) { this.clienteNombre = clienteNombre; }
    public String getTienda() { return tienda; }
    public void setTienda(String tienda) { this.tienda = tienda; }


    public double getMontoCompra() { return montoCompra; }
    public void setMontoCompra(double montoCompra) { this.montoCompra = montoCompra; calcularTotalGeneral(); }
    public double getGanancia() { return ganancia; }
    public void setGanancia(double ganancia) { this.ganancia = ganancia; calcularTotalGeneral(); }
    public double getTotalGeneral() { return totalGeneral; }
    public void setTotalGeneral(double totalGeneral) { this.totalGeneral = totalGeneral; }


    public Date getFechaEntrega() { return fechaEntrega; }
    public void setFechaEntrega(Date fechaEntrega) { this.fechaEntrega = fechaEntrega; }
    public Date getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(Date fechaCreacion) { this.fechaCreacion = fechaCreacion; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public String getNotas() { return notas; }
    public void setNotas(String notas) { this.notas = notas; }
    public Integer getTarjetaId() { return tarjetaId; }
    public void setTarjetaId(Integer tarjetaId) { this.tarjetaId = tarjetaId; }
    public String getTarjetaAlias() { return tarjetaAlias; }
    public void setTarjetaAlias(String tarjetaAlias) { this.tarjetaAlias = tarjetaAlias; }


    private void calcularTotalGeneral() { this.totalGeneral = this.montoCompra + this.ganancia; }
    public double calcularPorcentajeGanancia() { return montoCompra == 0 ? 0 : (ganancia / montoCompra) * 100.0; }
    public boolean estaAtrasado() { return fechaEntrega != null && ESTADO_PENDIENTE.equals(estado) && fechaEntrega.before(new Date()); }
    public boolean puedeMarcarComoEntregado() { return ESTADO_PENDIENTE.equals(estado); }
    public boolean puedeMarcarComoPagado() { return ESTADO_ENTREGADO.equals(estado); }
}
