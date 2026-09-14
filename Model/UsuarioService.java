package com.DJuanCarlosPeluqueria.DJuanCarlosPeluqueria.Model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.DJuanCarlosPeluqueria.DJuanCarlosPeluqueria.Model.Repository.UsuarioRepository;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    public List<Usuario> getAllUsuarios() {
        return usuarioRepository.findAll();
    }

    public List<UsuarioDTO> getAllUsuariosDTO() {
        return usuarioRepository.findAll().stream()
                .map(UsuarioDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public Page<Usuario> getUsuariosPaginados(Pageable pageable) {
        return usuarioRepository.findAll(pageable);
    }

    public Page<UsuarioDTO> getUsuariosPaginadosDTO(Pageable pageable) {
        return usuarioRepository.findAll(pageable)
                .map(UsuarioDTO::fromEntity);
    }

    public Usuario getUsuarioById(Long id) {
        return usuarioRepository.findById(id).orElse(null);
    }

    public UsuarioDTO getUsuarioByIdDTO(Long id) {
        Usuario usuario = usuarioRepository.findById(id).orElse(null);
        return UsuarioDTO.fromEntity(usuario);
    }

    public List<Usuario> obtenerPorRol(Long idRol) {
        return usuarioRepository.findByIdRolFK(idRol);
    }

    public List<UsuarioDTO> obtenerPorRolDTO(Long idRol) {
        return usuarioRepository.findByIdRolFK(idRol).stream()
                .map(UsuarioDTO::fromEntity)
                .collect(Collectors.toList());
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
        return usuarioRepository.save(usuario);
    }

    public UsuarioDTO createUsuarioDTO(Usuario usuario) {
        Usuario saved = createUsuario(usuario);
        return UsuarioDTO.fromEntity(saved);
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
            existingUsuario.setContrasenia(usuario.getContrasenia());
            existingUsuario.setEstado(usuario.getEstado());
            existingUsuario.setIdRolFK(usuario.getIdRolFK());

            // Guarda y retorna el objeto actualizado
            return usuarioRepository.save(existingUsuario);
        }
        return null;
    }

    public UsuarioDTO updateUsuarioDTO(Long id, Usuario usuario) {
        Usuario updated = updateUsuario(id, usuario);
        return UsuarioDTO.fromEntity(updated);
    }

    public void deleteUsuario(Long id) {
        usuarioRepository.deleteById(id);
    }
}