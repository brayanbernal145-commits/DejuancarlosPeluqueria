package com.DJuanCarlosPeluqueria.DJuanCarlosPeluqueria.Model.Repository;

import com.DJuanCarlosPeluqueria.DJuanCarlosPeluqueria.Model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Usuario findByEmail(String email);
    List<Usuario> findByIdRolFK(Long idRolFK);
    Usuario findByNumDocumento(Long numDocumento);
}


