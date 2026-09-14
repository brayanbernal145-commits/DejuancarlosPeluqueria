package com.DJuanCarlosPeluqueria.DJuanCarlosPeluqueria.Model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.DJuanCarlosPeluqueria.DJuanCarlosPeluqueria.Model.Repository.UsuarioRepository;
import com.DJuanCarlosPeluqueria.DJuanCarlosPeluqueria.Controller.dto.UsuarioCreateDTO;
import com.DJuanCarlosPeluqueria.DJuanCarlosPeluqueria.Controller.dto.UsuarioUpdateDTO;
import com.DJuanCarlosPeluqueria.DJuanCarlosPeluqueria.Controller.dto.AdminUsuarioUpdateDTO;
import java.util.List;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

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
    
    /**
     * Create a new user from DTO with password hashing
     */
    public Usuario createUsuario(UsuarioCreateDTO dto) {
        Usuario usuario = new Usuario();
        usuario.setTipoDocumento(dto.getTipoDocumento());
        usuario.setNumDocumento(dto.getNumDocumento());
        usuario.setNombreCompleto(dto.getNombreCompleto());
        usuario.setTelefono(dto.getTelefono());
        usuario.setEmail(dto.getEmail());
        usuario.setDireccion(dto.getDireccion());
        usuario.setGenero(dto.getGenero());
        
        // Hash password before storing
        if (dto.getContrasenia() != null && !dto.getContrasenia().isEmpty()) {
            usuario.setContrasenia(PasswordHasher.hashPassword(dto.getContrasenia()));
        }
        
        // Set defaults for security-sensitive fields
        usuario.setEstado("Activo");
        usuario.setCargo("Cliente");
        usuario.setIdRolFK(3); // Default role: Cliente
        
        return usuarioRepository.save(usuario);
    }

    /**
     * Update user profile (non-sensitive fields only)
     */
    public Usuario updateUsuarioProfile(Long id, UsuarioUpdateDTO dto) {
        Usuario existingUsuario = usuarioRepository.findById(id).orElse(null);
        if (existingUsuario != null) {
            if (dto.getTipoDocumento() != null) {
                existingUsuario.setTipoDocumento(dto.getTipoDocumento());
            }
            if (dto.getNumDocumento() != null) {
                existingUsuario.setNumDocumento(dto.getNumDocumento());
            }
            if (dto.getNombreCompleto() != null) {
                existingUsuario.setNombreCompleto(dto.getNombreCompleto());
            }
            if (dto.getTelefono() != null) {
                existingUsuario.setTelefono(dto.getTelefono());
            }
            if (dto.getEmail() != null) {
                existingUsuario.setEmail(dto.getEmail());
            }
            if (dto.getDireccion() != null) {
                existingUsuario.setDireccion(dto.getDireccion());
            }
            if (dto.getGenero() != null) {
                existingUsuario.setGenero(dto.getGenero());
            }
            
            return usuarioRepository.save(existingUsuario);
        }
        return null;
    }
    
    /**
     * Admin update - can modify all fields including sensitive ones
     */
    public Usuario adminUpdateUsuario(Long id, AdminUsuarioUpdateDTO dto) {
        Usuario existingUsuario = usuarioRepository.findById(id).orElse(null);
        if (existingUsuario != null) {
            if (dto.getTipoDocumento() != null) {
                existingUsuario.setTipoDocumento(dto.getTipoDocumento());
            }
            if (dto.getNumDocumento() != null) {
                existingUsuario.setNumDocumento(dto.getNumDocumento());
            }
            if (dto.getNombreCompleto() != null) {
                existingUsuario.setNombreCompleto(dto.getNombreCompleto());
            }
            if (dto.getTelefono() != null) {
                existingUsuario.setTelefono(dto.getTelefono());
            }
            if (dto.getEmail() != null) {
                existingUsuario.setEmail(dto.getEmail());
            }
            if (dto.getDireccion() != null) {
                existingUsuario.setDireccion(dto.getDireccion());
            }
            if (dto.getGenero() != null) {
                existingUsuario.setGenero(dto.getGenero());
            }
            if (dto.getCargo() != null) {
                existingUsuario.setCargo(dto.getCargo());
            }
            if (dto.getEstado() != null) {
                existingUsuario.setEstado(dto.getEstado());
            }
            if (dto.getIdRolFK() != null) {
                existingUsuario.setIdRolFK(dto.getIdRolFK());
            }
            
            return usuarioRepository.save(existingUsuario);
        }
        return null;
    }
    
    /**
     * Change user password with verification
     */
    public boolean changePassword(Long userId, String currentPassword, String newPassword) {
        Usuario usuario = usuarioRepository.findById(userId).orElse(null);
        if (usuario == null) {
            return false;
        }
        
        // Verify current password
        if (!PasswordHasher.verifyPassword(currentPassword, usuario.getContrasenia())) {
            return false;
        }
        
        // Hash and set new password
        usuario.setContrasenia(PasswordHasher.hashPassword(newPassword));
        usuarioRepository.save(usuario);
        return true;
    }
    
    /**
     * Admin password reset (no current password verification needed)
     */
    public boolean adminResetPassword(Long userId, String newPassword) {
        Usuario usuario = usuarioRepository.findById(userId).orElse(null);
        if (usuario == null) {
            return false;
        }
        
        usuario.setContrasenia(PasswordHasher.hashPassword(newPassword));
        usuarioRepository.save(usuario);
        return true;
    }

    public void deleteUsuario(Long id) {
        usuarioRepository.deleteById(id);
    }
}