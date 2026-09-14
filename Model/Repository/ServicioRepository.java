package com.DJuanCarlosPeluqueria.DJuanCarlosPeluqueria.Model.Repository;

import com.DJuanCarlosPeluqueria.DJuanCarlosPeluqueria.Model.Servicios;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

@Repository
public interface ServicioRepository extends JpaRepository<Servicios, Long> {
    List<Servicios> findByNombreServicio(String nombreServicio);
    @Query("SELECT s FROM Servicios s WHERE " +
            "(:filtrarCat = false OR LOWER(s.categoria) IN :categorias) AND " +
            "(LOWER(s.estado) LIKE LOWER(CONCAT('%', :estado, '%')))")
    Page<Servicios> buscarPorCategoriasYEstado(
            @Param("filtrarCat") boolean filtrarCat,
            @Param("categorias") List<String> categorias,
            @Param("estado") String estado,
            Pageable pageable);
}


