package com.DJuanCarlosPeluqueria.DJuanCarlosPeluqueria.Model;

import java.time.LocalDate;

/**
 * Data Transfer Object for Usuario entity.
 * Excludes sensitive fields like password from API responses.
 */
public class UsuarioDTO {

    private Long idUsuario;
    private String tipoDocumento;
    private Long numDocumento;
    private String nombreCompleto;
    private Long telefono;
    private String email;
    private String direccion;
    private String genero;
    private String cargo;
    private String estado;
    private LocalDate fechaRegistro;
    private int idRolFK;

    public UsuarioDTO() {}

    public UsuarioDTO(Long idUsuario, String tipoDocumento, Long numDocumento, String nombreCompleto,
                      Long telefono, String email, String direccion, String genero, String cargo,
                      String estado, LocalDate fechaRegistro, int idRolFK) {
        this.idUsuario = idUsuario;
        this.tipoDocumento = tipoDocumento;
        this.numDocumento = numDocumento;
        this.nombreCompleto = nombreCompleto;
        this.telefono = telefono;
        this.email = email;
        this.direccion = direccion;
        this.genero = genero;
        this.cargo = cargo;
        this.estado = estado;
        this.fechaRegistro = fechaRegistro;
        this.idRolFK = idRolFK;
    }

    /**
     * Factory method to create DTO from Usuario entity.
     * Explicitly excludes the password field.
     */
    public static UsuarioDTO fromEntity(Usuario usuario) {
        if (usuario == null) {
            return null;
        }
        return new UsuarioDTO(
            usuario.getIdUsuario(),
            usuario.getTipoDocumento(),
            usuario.getNumDocumento(),
            usuario.getNombreCompleto(),
            usuario.getTelefono(),
            usuario.getEmail(),
            usuario.getDireccion(),
            usuario.getGenero(),
            usuario.getCargo(),
            usuario.getEstado(),
            usuario.getFechaRegistro(),
            usuario.getIdRolFK()
        );
    }

    // Getters and Setters
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
