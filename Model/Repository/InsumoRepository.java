package com.DJuanCarlosPeluqueria.DJuanCarlosPeluqueria.Model.Repository;

import com.DJuanCarlosPeluqueria.DJuanCarlosPeluqueria.Model.Insumo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface InsumoRepository extends JpaRepository<Insumo, Long> {

    @Query("SELECT i FROM Insumo i WHERE " +
            "(:filtrarCat = false OR LOWER(i.categoriaInsumo) LIKE LOWER(CONCAT('%', :categoria, '%'))) AND " +
            "(:filtrarEst = false OR " +
            " (:estado = 'Disponible' AND i.stockActual > 0) OR " +
            " (:estado = 'No Disponible' AND i.stockActual <= 0))")
    Page<Insumo> buscarPorCategoriasYEstado(
            @Param("filtrarCat") boolean filtrarCat,
            @Param("categoria") String categoria,
            @Param("filtrarEst") boolean filtrarEst,
            @Param("estado") String estado,
            Pageable pageable);
}