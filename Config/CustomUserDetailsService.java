package com.DJuanCarlosPeluqueria.DJuanCarlosPeluqueria.Config;

import com.DJuanCarlosPeluqueria.DJuanCarlosPeluqueria.Model.Usuario;
import com.DJuanCarlosPeluqueria.DJuanCarlosPeluqueria.Model.UsuarioService;
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
    private UsuarioService usuarioService;

    @Override
    public UserDetails loadUserByUsername(String numDocumento) throws UsernameNotFoundException {
        try {
            Long documento = Long.parseLong(numDocumento);
            Usuario usuario = usuarioService.obtenerPorNumDocumento(documento);
            
            if (usuario == null) {
                throw new UsernameNotFoundException("Usuario no encontrado con documento: " + numDocumento);
            }

            List<GrantedAuthority> authorities = new ArrayList<>();
            // Add role based on idRolFK
            authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
            if (usuario.getIdRolFK() == 1) {
                authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
            }

            return User.builder()
                    .username(String.valueOf(usuario.getNumDocumento()))
                    .password(usuario.getContrasenia())
                    .authorities(authorities)
                    .accountExpired(false)
                    .accountLocked(!"Activo".equals(usuario.getEstado()))
                    .credentialsExpired(false)
                    .disabled(!"Activo".equals(usuario.getEstado()))
                    .build();
        } catch (NumberFormatException e) {
            throw new UsernameNotFoundException("Número de documento inválido: " + numDocumento);
        }
    }
}
