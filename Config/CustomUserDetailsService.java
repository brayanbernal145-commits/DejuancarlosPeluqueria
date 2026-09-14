package com.DJuanCarlosPeluqueria.DJuanCarlosPeluqueria.Config;

import com.DJuanCarlosPeluqueria.DJuanCarlosPeluqueria.Model.Usuario;
import com.DJuanCarlosPeluqueria.DJuanCarlosPeluqueria.Model.Repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Try to parse username as document number
        Usuario usuario;
        try {
            Long numDocumento = Long.parseLong(username);
            usuario = usuarioRepository.findByNumDocumento(numDocumento);
        } catch (NumberFormatException e) {
            // If not a number, try email
            usuario = usuarioRepository.findByEmail(username);
        }

        if (usuario == null) {
            throw new UsernameNotFoundException("Usuario no encontrado: " + username);
        }

        // Check if user is active
        if (!"Activo".equalsIgnoreCase(usuario.getEstado())) {
            throw new UsernameNotFoundException("Usuario inactivo: " + username);
        }

        // Map role to authority
        List<GrantedAuthority> authorities = new ArrayList<>();
        String role = mapRoleIdToRoleName(usuario.getIdRolFK());
        authorities.add(new SimpleGrantedAuthority(role));

        return User.builder()
                .username(String.valueOf(usuario.getNumDocumento()))
                .password(usuario.getContrasenia())
                .authorities(authorities)
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(!"Activo".equalsIgnoreCase(usuario.getEstado()))
                .build();
    }

    private String mapRoleIdToRoleName(int roleId) {
        // Map role IDs to role names based on the application's role structure
        switch (roleId) {
            case 1:
                return "ROLE_ADMIN";
            case 2:
                return "ROLE_EMPLOYEE";
            case 3:
                return "ROLE_CLIENT";
            default:
                return "ROLE_USER";
        }
    }
}
