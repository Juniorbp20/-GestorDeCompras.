package com.example.gestiondecompras.models;

import java.io.Serializable;
import java.util.Date;


public class Cliente implements Serializable {
    private int id;
    private String nombre;
    private String telefono;
    private String email;
    private Date fechaRegistro;


    public Cliente() { this.fechaRegistro = new Date(); }
    public Cliente(String nombre, String telefono, String email) {
        this(); this.nombre = nombre; this.telefono = telefono; this.email = email;
    }


    public int getId() { return id; }
    public void setId(int id) { this.id = id; }


    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }


    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }


    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }


    public Date getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(Date fechaRegistro) { this.fechaRegistro = fechaRegistro; }


    @Override public String toString() { return nombre; }
}