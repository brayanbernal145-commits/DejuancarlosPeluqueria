package com.DJuanCarlosPeluqueria.DJuanCarlosPeluqueria.Model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table (name ="factura")
public class Factura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column (name = "numFactura")
    private long numFactura;

    @Column (name = "fechaEmision")
    private LocalDate fechaEmision;

    @Column (name = "total")
    private BigDecimal total;

    @Column (name = "iva")
    private BigDecimal iva;

    @Column (name = "estadoFactura")
    private String estadoFactura ;

    @Column (name = "metodoPago")
    private String metodoPago;

    @Column (name = "observaciones")
    private String observaciones;

    @Column(name = "idCitaFK")
    private int idCitaFK;

    public Factura(){}

    public Factura (LocalDate fechaEmision, BigDecimal total, BigDecimal iva,
                    String estadoFactura, String metodoPago, String observaciones, int idCitaFK){
        this.fechaEmision = fechaEmision;
        this.total = total;
        this.iva = iva;
        this.estadoFactura = estadoFactura;
        this.metodoPago = metodoPago;
        this.observaciones = observaciones;
        this.idCitaFK = idCitaFK;
    }

    public long getNumFactura() {
        return numFactura;
    }

    public void setNumFactura(long numFactura) {
        this.numFactura = numFactura;
    }

    public LocalDate getFechaEmision() {
        return fechaEmision;
    }

    public void setFechaEmision(LocalDate fechaEmision) {
        this.fechaEmision = fechaEmision;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public BigDecimal getIva() {
        return iva;
    }

    public void setIva(BigDecimal iva) {
        this.iva = iva;
    }

    public String getEstadoFactura() {
        return estadoFactura;
    }

    public void setEstadoFactura(String estadoFactura) {
        this.estadoFactura = estadoFactura;
    }

    public String getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(String metodoPago) {
        this.metodoPago = metodoPago;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public int getIdCitaFK() {
        return idCitaFK;
    }

    public void setIdCitaFK(int idCitaFK) {
        this.idCitaFK = idCitaFK;
    }
}
