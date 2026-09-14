package com.DJuanCarlosPeluqueria.DJuanCarlosPeluqueria.Model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;

@Entity
@Table(name = "servicio")
public class Servicios {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idServicio")
    private Long idServicio;

    @Column(name = "nombreServicio")
    private String nombreServicio;

    @Column(name = "descripcionServicio")
    private String descripcionServicio;

    @Column(name = "precioBase")
    private Long precioBase;

    @Column(name = "categoria")
    private String categoria;

    @Column(name = "estado")
    private String estado = "Activo";
    // Forzar lectura exacta en JSON tanto para idUsuarioFk como idUsuarioFK
    @JsonProperty("idUsuarioFk")
    @Column(name = "idUsuarioFK")
    private Long idUsuarioFk;

    // Forzar lectura exacta del campo insumo independientemente de mayúsculas/minúsculas
    @JsonProperty("idInsumoFK")
    @Column(name = "idInsumoFK")
    private Integer idInsumoFK;


    public Servicios() {}

    public Servicios(Long idServicio, String nombreServicio, String descripcionServicio,
                     Long precioBase, String estado, String categoria, Long idUsuarioFk, Integer idInsumoFK) {
        this.idServicio = idServicio;
        this.nombreServicio = nombreServicio;
        this.descripcionServicio = descripcionServicio;
        this.precioBase = precioBase;
        this.categoria = categoria;
        this.estado = estado;
        this.idUsuarioFk = idUsuarioFk;
        this.idInsumoFK = idInsumoFK;
    }

    public Long getIdServicio() {
        return idServicio;
    }

    public void setIdServicio(Long idServicio) {
        this.idServicio = idServicio;
    }

    public String getNombreServicio() {
        return nombreServicio;
    }

    public void setNombreServicio(String nombreServicio) {
        this.nombreServicio = nombreServicio;
    }

    public String getDescripcionServicio() {
        return descripcionServicio;
    }

    public void setDescripcionServicio(String descripcionServicio) {
        this.descripcionServicio = descripcionServicio;
    }

    public Long getPrecioBase() {
        return precioBase;
    }

    public void setPrecioBase(Long precioBase) {
        this.precioBase = precioBase;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public Long getIdUsuarioFk() {
        return idUsuarioFk;
    }

    public void setIdUsuarioFk(Long idUsuarioFk) {
        this.idUsuarioFk = idUsuarioFk;
    }

    public Integer getIdInsumoFK() {
        return idInsumoFK;
    }

    public void setIdInsumoFK(Integer idInsumoFK) {
        this.idInsumoFK = idInsumoFK;
    }
}