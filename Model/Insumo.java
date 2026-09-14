package com.DJuanCarlosPeluqueria.DJuanCarlosPeluqueria.Model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "insumo") // Asegura la coincidencia exacta con el nombre de tu tabla en la BD
public class Insumo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idInsumo")
    private Long idInsumo;

    @Column(name = "nombreInsumo")
    private String nombreInsumo;

    @Column(name = "descripcionInsumo")
    private String descripcionInsumo;

    @Column(name = "categoriaInsumo")
    private String categoriaInsumo;

    @Column(name = "fechaVencimiento")
    private LocalDate fechaVencimiento;

    @Column(name = "stockActual")
    private int stockActual;

    @Column(name = "stockMinPosible")
    private int stockMinPosible;

    @Column(name = "horaActualizacion")
    private LocalTime horaActualizacion;

    // Constructor vacío obligatorio para JPA
    public Insumo() {}

    // Constructor con parámetros
    public Insumo(String nombreInsumo, String descripcionInsumo,
                  LocalDate fechaVencimiento, int stockActual, int stockMinPosible,
                  LocalTime horaActualizacion, String categoriaInsumo) {
        this.nombreInsumo = nombreInsumo;
        this.descripcionInsumo = descripcionInsumo;
        this.categoriaInsumo = categoriaInsumo;
        this.fechaVencimiento = fechaVencimiento;
        this.stockActual = stockActual;
        this.stockMinPosible = stockMinPosible;
        this.horaActualizacion = horaActualizacion;
    }

    // Getters y Setters
    public Long getIdInsumo() {
        return idInsumo;
    }

    public void setIdInsumo(Long idInsumo) {
        this.idInsumo = idInsumo;
    }

    public String getNombreInsumo() {
        return nombreInsumo;
    }

    public void setNombreInsumo(String nombreInsumo) {
        this.nombreInsumo = nombreInsumo;
    }

    public String getCategoriaInsumo() {
        return categoriaInsumo;
    }

    public void setCategoriaInsumo(String categoriaInsumo) {
        this.categoriaInsumo = categoriaInsumo;
    }

    public String getDescripcionInsumo() {
        return descripcionInsumo;
    }

    public void setDescripcionInsumo(String descripcionInsumo) {
        this.descripcionInsumo = descripcionInsumo;
    }

    public LocalDate getFechaVencimiento() {
        return fechaVencimiento;
    }

    public void setFechaVencimiento(LocalDate fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }

    public int getStockActual() {
        return stockActual;
    }

    public void setStockActual(int stockActual) {
        this.stockActual = stockActual;
    }

    public int getStockMinPosible() {
        return stockMinPosible;
    }

    public void setStockMinPosible(int stockMinPosible) {
        this.stockMinPosible = stockMinPosible;
    }

    public LocalTime getHoraActualizacion() {
        return horaActualizacion;
    }

    public void setHoraActualizacion(LocalTime horaActualizacion) {
        this.horaActualizacion = horaActualizacion;
    }
}