package com.DJuanCarlosPeluqueria.DJuanCarlosPeluqueria.Model;

import com.DJuanCarlosPeluqueria.DJuanCarlosPeluqueria.Model.Repository.CitaRepository;
import com.DJuanCarlosPeluqueria.DJuanCarlosPeluqueria.Model.Repository.UsuarioRepository;
import com.DJuanCarlosPeluqueria.DJuanCarlosPeluqueria.Model.Repository.ServicioRepository; // <-- Asegúrate de tener este import
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;

@Service
public class CitaService {

    @Autowired
    private CitaRepository citaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ServicioRepository servicioRepository; // <-- Inyecta el repositorio de servicio

    public List<Cita> getAllCitas() {
        return citaRepository.findAll();
    }

    public Cita getCitaById(Long id) {
        return citaRepository.findById(id).orElse(null);
    }

    public Cita createCita(Cita cita) {
        // 1. Validar que el total no vaya nulo
        if (cita.getTotalServicio() == null) {
            cita.setTotalServicio(BigDecimal.ZERO);
        }
        // 2. Validar que el Usuario exista obligatoriamente
        if (cita.getIdUsuarioFK() == null || !usuarioRepository.existsById(cita.getIdUsuarioFK())) {
            throw new IllegalArgumentException("El usuario con ID " + cita.getIdUsuarioFK() + " no existe o no fue seleccionado.");
        }

        // 3. Validar que el Servicio exista obligatoriamente (es NOT NULL en la BD)
        if (cita.getIdServicioFK() == null || !servicioRepository.existsById(cita.getIdServicioFK())) {
            throw new IllegalArgumentException("El servicio con ID " + cita.getIdServicioFK() + " no existe o no fue seleccionado.");
        }

        return citaRepository.save(cita);
    }
    public List<Cita> obtenerPorIdUsuario(Integer idUsuario) {
        // Si usas Spring Data JPA en la interfaz CitaRepository:
        return citaRepository.findByIdUsuarioFK(idUsuario);
    }
    public Cita updateCita(Long id, Cita cita) {
        Cita existingCita = citaRepository.findById(id).orElse(null);
        if (existingCita != null) {
            if (cita.getTotalServicio() == null) {
                cita.setTotalServicio(BigDecimal.ZERO);
            }

            if (cita.getIdUsuarioFK() != null && !usuarioRepository.existsById(cita.getIdUsuarioFK())) {
                throw new IllegalArgumentException("El usuario especificado no existe.");
            }

            if (cita.getIdServicioFK() != null && !servicioRepository.existsById(cita.getIdServicioFK())) {
                throw new IllegalArgumentException("El servicio especificado no existe.");
            }

            existingCita.setFecha(cita.getFecha());
            existingCita.setHoraInicio(cita.getHoraInicio());
            existingCita.setHoraFinal(cita.getHoraFinal());
            existingCita.setEstado(cita.getEstado());
            existingCita.setObservacion(cita.getObservacion());
            existingCita.setTotalServicio(cita.getTotalServicio());
            existingCita.setIdUsuarioFK(cita.getIdUsuarioFK());
            existingCita.setIdServicioFK(cita.getIdServicioFK());
            existingCita.setIdInsumoFK(cita.getIdInsumoFK());

            return citaRepository.save(existingCita);
        }
        return null;
    }

    public void deleteCita(Long id) {
        citaRepository.deleteById(id);
    }
}