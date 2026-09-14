package com.DJuanCarlosPeluqueria.DJuanCarlosPeluqueria.Model;

import com.DJuanCarlosPeluqueria.DJuanCarlosPeluqueria.Model.Repository.InsumoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InsumoService {

    @Autowired
    private InsumoRepository insumoRepository;

    public List<Insumo> getAllInsumos() {
        return insumoRepository.findAll();
    }

    public Insumo getInsumoById(Long id) {
        return insumoRepository.findById(id).orElse(null);
    }

    public Page<Insumo> buscarConFiltros(String categoria, String estado, Pageable pageable) {
        boolean filtrarCat = categoria != null && !categoria.trim().isEmpty() && !categoria.equalsIgnoreCase("TODAS");
        boolean filtrarEst = estado != null && !estado.trim().isEmpty() && !estado.equalsIgnoreCase("TODOS");

        String catFiltro = filtrarCat ? categoria.trim() : "";
        String estFiltro = filtrarEst ? estado.trim() : "";

        return insumoRepository.buscarPorCategoriasYEstado(filtrarCat, catFiltro, filtrarEst, estFiltro, pageable);
    }

    public Insumo createInsumo(Insumo insumo) {
        return insumoRepository.save(insumo);
    }

    public Insumo updateInsumo(Long id, Insumo insumo) {
        Insumo existingInsumo = insumoRepository.findById(id).orElse(null);
        if (existingInsumo != null) {
            existingInsumo.setNombreInsumo(insumo.getNombreInsumo());
            existingInsumo.setDescripcionInsumo(insumo.getDescripcionInsumo());
            existingInsumo.setCategoriaInsumo(insumo.getCategoriaInsumo());
            existingInsumo.setFechaVencimiento(insumo.getFechaVencimiento());
            existingInsumo.setStockActual(insumo.getStockActual());
            existingInsumo.setStockMinPosible(insumo.getStockMinPosible());
            existingInsumo.setHoraActualizacion(insumo.getHoraActualizacion());

            return insumoRepository.save(existingInsumo);
        }
        return null;
    }

    public void deleteInsumo(Long id) {
        insumoRepository.deleteById(id);
    }
}