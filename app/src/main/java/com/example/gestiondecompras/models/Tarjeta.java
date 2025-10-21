package com.example.gestiondecompras.models;

import java.io.Serializable;
import java.util.Date;

public class Tarjeta implements Serializable {
    private int id;
    private String banco;
    private String alias;          // Ej: "VISA ****1234"
    private double limiteCredito;  // Límite
    private double deudaActual;    // Adeudado
    private int diaCorte;          // 1–31
    private int diaVencimiento;    // 1–31
    private String notas;
    private Date fechaRegistro;

    public Tarjeta() { this.fechaRegistro = new Date(); }
    public Tarjeta(String banco, String alias, double limiteCredito, int diaCorte, int diaVencimiento) {
        this(); this.banco=banco; this.alias=alias; this.limiteCredito=limiteCredito; this.diaCorte=diaCorte; this.diaVencimiento=diaVencimiento; this.deudaActual=0.0;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getBanco() { return banco; }
    public void setBanco(String banco) { this.banco = banco; }
    public String getAlias() { return alias; }
    public void setAlias(String alias) { this.alias = alias; }
    public double getLimiteCredito() { return limiteCredito; }
    public void setLimiteCredito(double limiteCredito) { this.limiteCredito = limiteCredito; }
    public double getDeudaActual() { return deudaActual; }
    public void setDeudaActual(double deudaActual) { this.deudaActual = deudaActual; }
    public int getDiaCorte() { return diaCorte; }
    public void setDiaCorte(int diaCorte) { this.diaCorte = diaCorte; }
    public int getDiaVencimiento() { return diaVencimiento; }
    public void setDiaVencimiento(int diaVencimiento) { this.diaVencimiento = diaVencimiento; }
    public String getNotas() { return notas; }
    public void setNotas(String notas) { this.notas = notas; }
    public Date getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(Date fechaRegistro) { this.fechaRegistro = fechaRegistro; }

    @Override public String toString() { return banco + " - " + (alias != null ? alias : ""); }
}