package com.DJuanCarlosPeluqueria.DJuanCarlosPeluqueria.Model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "usuario")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idUsuario")
    private Long idUsuario;

    @Column(name = "tipoDocumento")
    private String tipoDocumento;

    @Column(name = "numDocumento")
    private Long numDocumento;

    @Column(name = "nombreCompleto")
    private String nombreCompleto;

    @Column(name = "telefono")
    private Long telefono;

    @Column(name = "email")
    private String email;

    @Column(name = "direccion")
    private String direccion;

    @Column(name = "genero")
    private String genero;

    @Column(name = "cargo")
    private String cargo = "Cliente";

    @Column(name = "contrasenia")
    private String contrasenia;

    @Column(name = "estado")
    private String estado = "Activo";

    @Column(name = "fechaRegistro")
    private LocalDate fechaRegistro = LocalDate.now();

    @Column(name = "idRolFK")
    private int idRolFK =  3;

    public Usuario() {}

    public Usuario(Long idUsuario, String tipoDocumento, Long numDocumento, String nombreCompleto,
                   Long telefono, String email, String direccion, String genero, String cargo,
                   String contrasenia, String estado, LocalDate fechaRegistro, int idRolFK) {
        this.idUsuario = idUsuario;
        this.tipoDocumento = tipoDocumento;
        this.numDocumento = numDocumento;
        this.nombreCompleto = nombreCompleto;
        this.telefono = telefono;
        this.email = email;
        this.direccion = direccion;
        this.genero = genero;
        this.cargo = cargo;
        this.contrasenia = contrasenia;
        this.estado = estado;
        this.fechaRegistro = fechaRegistro;
        this.idRolFK = idRolFK;
    }

    // Getters y Setters
    public Long getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Long idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(String tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    public Long getNumDocumento() {
        return numDocumento;
    }

    public void setNumDocumento(Long numDocumento) {
        this.numDocumento = numDocumento;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public Long getTelefono() {
        return telefono;
    }

    public void setTelefono(Long telefono) {
        this.telefono = telefono;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    public String getContrasenia() {
        return contrasenia;
    }

    public void setContrasenia(String contrasenia) {
        this.contrasenia = contrasenia;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public LocalDate getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDate fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public int getIdRolFK() {
        return idRolFK;
    }

    public void setIdRolFK(int idRolFK) {
        this.idRolFK = idRolFK;
    }
}