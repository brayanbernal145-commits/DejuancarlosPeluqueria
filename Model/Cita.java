package com.DJuanCarlosPeluqueria.DJuanCarlosPeluqueria.Model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "cita")
public class Cita {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idCita")
    private Long idCita;

    @Column(name = "fecha", nullable = false)
    private LocalDate fecha;

    @Column(name = "horaInicio", nullable = false)
    private LocalTime horaInicio;

    @Column(name = "horaFinal", nullable = false)
    private LocalTime horaFinal;

    @Column(name = "estado", nullable = false, length = 20)
    private String estado;

    @Column(name = "observacion", length = 50)
    private String observacion;

    @Column(name = "totalServicio", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalServicio;

    @Column(name = "idUsuarioFK", nullable = false)
    private Long idUsuarioFK;

    @Column(name = "idServicioFK", nullable = false)
    private Long idServicioFK;

    @Column(name = "idInsumoFK", nullable = true)
    private Long idInsumoFK;

    @Column(name = "servicio", nullable = false, length = 100)
    private String servicio;

    public Cita() {
    }

    public Cita(Long idCita, LocalDate fecha, LocalTime horaInicio, LocalTime horaFinal, String estado, String observacion, BigDecimal totalServicio, Long idUsuarioFK, Long idServicioFK, Long idInsumoFK) {
        this.idCita = idCita;
        this.fecha = fecha;
        this.horaInicio = horaInicio;
        this.horaFinal = horaFinal;
        this.estado = estado;
        this.observacion = observacion;
        this.totalServicio = totalServicio;
        this.idUsuarioFK = idUsuarioFK;
        this.idServicioFK = idServicioFK;
        this.idInsumoFK = idInsumoFK;
    }

    public Long getIdCita() {
        return idCita;
    }

    public void setIdCita(Long idCita) {
        this.idCita = idCita;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public LocalTime getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(LocalTime horaInicio) {
        this.horaInicio = horaInicio;
    }

    public LocalTime getHoraFinal() {
        return horaFinal;
    }

    public void setHoraFinal(LocalTime horaFinal) {
        this.horaFinal = horaFinal;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    public BigDecimal getTotalServicio() {
        return totalServicio;
    }

    public void setTotalServicio(BigDecimal totalServicio) {
        this.totalServicio = totalServicio;
    }

    public String getServicio() {
        return servicio;
    }

    public void setServicio(String servicio) {
        this.servicio = servicio;
    }

    public Long getIdUsuarioFK() {
        return idUsuarioFK;
    }

    public void setIdUsuarioFK(Long idUsuarioFK) {
        this.idUsuarioFK = idUsuarioFK;
    }

    public Long getIdServicioFK() {
        return idServicioFK;
    }

    public void setIdServicioFK(Long idServicioFK) {
        this.idServicioFK = idServicioFK;
    }

    public Long getIdInsumoFK() {
        return idInsumoFK;
    }

    public void setIdInsumoFK(Long idInsumoFK) {
        this.idInsumoFK = idInsumoFK;
    }
}