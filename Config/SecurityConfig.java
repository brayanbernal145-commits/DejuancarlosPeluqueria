package com.DJuanCarlosPeluqueria.DJuanCarlosPeluqueria.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.disable())
            .authorizeHttpRequests(auth -> auth
                // Allow public GET access to read-only endpoints
                .requestMatchers(HttpMethod.GET, "/api/servicios/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/insumos/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/citas/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/usuarios/**").permitAll()
                
                // Allow user registration and login
                .requestMatchers(HttpMethod.POST, "/api/usuarios/login").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/usuarios").permitAll()
                
                // Allow public access to static resources
                .requestMatchers("/Static/**", "/css/**", "/javascript/**", "/icons/**").permitAll()
                .requestMatchers("/", "/index.html").permitAll()
                
                // Require authentication for all mutating operations
                .requestMatchers(HttpMethod.POST, "/api/servicios/**").authenticated()
                .requestMatchers(HttpMethod.PUT, "/api/servicios/**").authenticated()
                .requestMatchers(HttpMethod.DELETE, "/api/servicios/**").authenticated()
                
                .requestMatchers(HttpMethod.POST, "/api/insumos/**").authenticated()
                .requestMatchers(HttpMethod.PUT, "/api/insumos/**").authenticated()
                .requestMatchers(HttpMethod.DELETE, "/api/insumos/**").authenticated()
                
                .requestMatchers(HttpMethod.POST, "/api/citas/**").authenticated()
                .requestMatchers(HttpMethod.PUT, "/api/citas/**").authenticated()
                .requestMatchers(HttpMethod.DELETE, "/api/citas/**").authenticated()
                
                .requestMatchers(HttpMethod.PUT, "/api/usuarios/**").authenticated()
                .requestMatchers(HttpMethod.DELETE, "/api/usuarios/**").authenticated()
                
                // Deny all other requests by default
                .anyRequest().authenticated()
            )
            .httpBasic(basic -> {})
            .sessionManagement(session -> 
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            );

        return http.build();
    }
}
