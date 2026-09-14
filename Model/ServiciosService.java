package com.DJuanCarlosPeluqueria.DJuanCarlosPeluqueria.Model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.DJuanCarlosPeluqueria.DJuanCarlosPeluqueria.Model.Repository.ServicioRepository;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ServiciosService {

    @Autowired
    private ServicioRepository servicioRepository;

    public List<Servicios> getAllservicios() {
        return servicioRepository.findAll();
    }

    public Page<Servicios> getServiciosPaginados(Pageable pageable) {
        return servicioRepository.findAll(pageable);
    }
    public Page<Servicios> buscarConFiltros(String categoria, String estado, Pageable pageable) {
        String estFiltro = (estado == null || estado.equalsIgnoreCase("TODOS")) ? "" : estado;

        // Si viene nulo, vacío o "TODAS", no se filtra por categoría
        if (categoria == null || categoria.trim().isEmpty() || categoria.equalsIgnoreCase("TODAS")) {
            return servicioRepository.buscarPorCategoriasYEstado(false, List.of(""), estFiltro, pageable);
        }

        // Convertimos "Manicura,Uñas,Pedicure" en una lista ["manicura", "uñas", "pedicure"]
        List<String> listaCategorias = Arrays.stream(categoria.split(","))
                .map(String::trim)
                .map(String::toLowerCase)
                .collect(Collectors.toList());

        return servicioRepository.buscarPorCategoriasYEstado(true, listaCategorias, estFiltro, pageable);
    }

    public Servicios getServicioById(Long id) {
        return servicioRepository.findById(id).orElse(null);
    }

    public Servicios createServicio(Servicios servicio) {
        if (servicio.getEstado() == null || servicio.getEstado().isEmpty()) {
            servicio.setEstado("Activo");
        }
        return servicioRepository.save(servicio);
    }

    public Servicios updateServicio(Long id, Servicios servicio) {
        Servicios servicioExistente = servicioRepository.findById(id).orElse(null);
        if (servicioExistente != null) {
            servicioExistente.setNombreServicio(servicio.getNombreServicio());
            servicioExistente.setDescripcionServicio(servicio.getDescripcionServicio());
            servicioExistente.setPrecioBase(servicio.getPrecioBase());
            servicioExistente.setEstado(servicio.getEstado());
            servicioExistente.setCategoria(servicio.getCategoria());
            servicioExistente.setIdUsuarioFk(servicio.getIdUsuarioFk());
            servicioExistente.setIdInsumoFK(servicio.getIdInsumoFK());

            return servicioRepository.save(servicioExistente);
        }
        return null;
    }

    public void deleteServicio(Long id) {
        servicioRepository.deleteById(id);
    }
}