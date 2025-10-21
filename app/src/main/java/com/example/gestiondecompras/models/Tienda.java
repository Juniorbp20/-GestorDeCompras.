package com.example.gestiondecompras.models;

import java.io.Serializable;
import java.util.Date;


public class Tienda implements Serializable {
    private int id;
    private String nombre;
    private String url;
    private boolean activa;
    private Date fechaRegistro;


    public static final String TEMU = "Temu";
    public static final String SHEIN = "Shein";
    public static final String AMAZON = "Amazon";
    public static final String ALIEXPRESS = "AliExpress";
    public static final String OTRA = "Otra";


    public Tienda() { this.fechaRegistro = new Date(); this.activa = true; }
    public Tienda(String nombre, String url) { this(); this.nombre = nombre; this.url = url; }


    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
    public boolean isActiva() { return activa; }
    public void setActiva(boolean activa) { this.activa = activa; }
    public Date getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(Date fechaRegistro) { this.fechaRegistro = fechaRegistro; }


    public static String[] getTiendasPredefinidas() {
        return new String[]{TEMU, SHEIN, AMAZON, ALIEXPRESS, OTRA};
    }


    @Override public String toString() { return nombre; }
    @Override public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Tienda t = (Tienda) obj;
        return nombre != null && nombre.equalsIgnoreCase(t.nombre);
    }
    @Override public int hashCode() { return nombre == null ? 0 : nombre.toLowerCase().hashCode(); }
}