package com.DJuanCarlosPeluqueria.DJuanCarlosPeluqueria.Model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.DJuanCarlosPeluqueria.DJuanCarlosPeluqueria.Model.Repository.UsuarioRepository;
import java.util.List;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<Usuario> getAllUsuarios() {
        return usuarioRepository.findAll();
    }

    public Page<Usuario> getUsuariosPaginados(Pageable pageable) {
        return usuarioRepository.findAll(pageable);
    }

    public Usuario getUsuarioById(Long id) {
        return usuarioRepository.findById(id).orElse(null);
    }

    public List<Usuario> obtenerPorRol(Long idRol) {
        return usuarioRepository.findByIdRolFK(idRol);
    }
    public Usuario obtenerPorNumDocumento(Long numDocumento) {
        return usuarioRepository.findByNumDocumento(numDocumento);
    }
    public Usuario createUsuario(Usuario usuario) {
        if (usuario.getEstado() == null || usuario.getEstado().isEmpty()) {
            usuario.setEstado("Activo");
        }
        if (usuario.getCargo() == null || usuario.getCargo().isEmpty()) {
            usuario.setCargo("Cliente");
        }
        // Encode password before saving
        if (usuario.getContrasenia() != null && !usuario.getContrasenia().isEmpty()) {
            usuario.setContrasenia(passwordEncoder.encode(usuario.getContrasenia()));
        }
        return usuarioRepository.save(usuario);
    }

    public Usuario updateUsuario(Long id, Usuario usuario) {
        Usuario existingUsuario = usuarioRepository.findById(id).orElse(null);
        if (existingUsuario != null) {
            existingUsuario.setTipoDocumento(usuario.getTipoDocumento());
            existingUsuario.setNumDocumento(usuario.getNumDocumento());
            existingUsuario.setNombreCompleto(usuario.getNombreCompleto());
            existingUsuario.setTelefono(usuario.getTelefono());
            existingUsuario.setEmail(usuario.getEmail());
            existingUsuario.setDireccion(usuario.getDireccion());
            existingUsuario.setGenero(usuario.getGenero());
            existingUsuario.setCargo(usuario.getCargo());
            // Only update password if provided and encode it
            if (usuario.getContrasenia() != null && !usuario.getContrasenia().isEmpty()) {
                existingUsuario.setContrasenia(passwordEncoder.encode(usuario.getContrasenia()));
            }
            existingUsuario.setEstado(usuario.getEstado());
            existingUsuario.setIdRolFK(usuario.getIdRolFK());

            // Guarda y retorna el objeto actualizado
            return usuarioRepository.save(existingUsuario);
        }
        return null;
    }

    public void deleteUsuario(Long id) {
        usuarioRepository.deleteById(id);
    }
}