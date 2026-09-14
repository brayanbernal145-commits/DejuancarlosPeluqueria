package com.DJuanCarlosPeluqueria.DJuanCarlosPeluqueria.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.DJuanCarlosPeluqueria.DJuanCarlosPeluqueria.Model.UsuarioService;
import com.DJuanCarlosPeluqueria.DJuanCarlosPeluqueria.Model.Usuario;
import com.DJuanCarlosPeluqueria.DJuanCarlosPeluqueria.Model.PdfService;
import com.DJuanCarlosPeluqueria.DJuanCarlosPeluqueria.Model.AuthenticationService;
import com.DJuanCarlosPeluqueria.DJuanCarlosPeluqueria.Model.PasswordHasher;
import com.DJuanCarlosPeluqueria.DJuanCarlosPeluqueria.Controller.dto.UsuarioCreateDTO;
import com.DJuanCarlosPeluqueria.DJuanCarlosPeluqueria.Controller.dto.UsuarioUpdateDTO;
import com.DJuanCarlosPeluqueria.DJuanCarlosPeluqueria.Controller.dto.AdminUsuarioUpdateDTO;
import com.DJuanCarlosPeluqueria.DJuanCarlosPeluqueria.Controller.dto.PasswordChangeDTO;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "*", allowedHeaders = "*", exposedHeaders = "X-Auth-Token")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;
    @Autowired
    private PdfService pdfService;
    @Autowired
    private AuthenticationService authenticationService;

    /**
     * Public login endpoint - returns session token
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        try {
            String numDocumentoStr = credentials.get("numDocumento");
            String contrasenia = credentials.get("contrasenia");

            if (numDocumentoStr == null || contrasenia == null) {
                return ResponseEntity.badRequest().body("Por favor ingresa documento y contraseña.");
            }

            Long numDocumento = Long.parseLong(numDocumentoStr);

            // Find user by document number
            Usuario usuario = usuarioService.obtenerPorNumDocumento(numDocumento);

            if (usuario != null && PasswordHasher.verifyPassword(contrasenia, usuario.getContrasenia())) {
                // Create session
                String token = authenticationService.createSession(usuario.getIdUsuario(), usuario.getIdRolFK());
                
                // Clear password before responding
                usuario.setContrasenia(null);
                
                // Return user data and token
                Map<String, Object> response = new HashMap<>();
                response.put("user", usuario);
                response.put("token", token);
                
                return ResponseEntity.ok()
                        .header("X-Auth-Token", token)
                        .body(response);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Número de documento o contraseña incorrectos.");
            }
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body("El número de documento debe ser numérico.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error en el servidor al intentar iniciar sesión.");
        }
    }
    
    /**
     * Logout endpoint
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader(value = "X-Auth-Token", required = false) String token) {
        if (token != null) {
            authenticationService.invalidateSession(token);
        }
        return ResponseEntity.ok("Sesión cerrada exitosamente.");
    }
    
    /**
     * Public registration endpoint - creates new user with Cliente role
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UsuarioCreateDTO usuarioDTO) {
        try {
            // Check if user already exists
            if (usuarioDTO.getNumDocumento() != null) {
                Usuario existing = usuarioService.obtenerPorNumDocumento(usuarioDTO.getNumDocumento());
                if (existing != null) {
                    return ResponseEntity.status(HttpStatus.CONFLICT)
                            .body("Ya existe un usuario con ese número de documento.");
                }
            }
            
            Usuario usuario = usuarioService.createUsuario(usuarioDTO);
            usuario.setContrasenia(null); // Don't return password
            return ResponseEntity.status(HttpStatus.CREATED).body(usuario);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al crear el usuario.");
        }
    }

    /**
     * Get all users - requires authentication and admin role
     */
    @GetMapping
    public ResponseEntity<?> getallUsuario(@RequestHeader(value = "X-Auth-Token", required = false) String token) {
        Long userId = authenticationService.validateSession(token);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Autenticación requerida.");
        }
        
        if (!authenticationService.isAdmin(token)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Acceso denegado. Se requieren privilegios de administrador.");
        }
        
        List<Usuario> usuarios = usuarioService.getAllUsuarios();
        // Clear passwords before returning
        usuarios.forEach(u -> u.setContrasenia(null));
        return ResponseEntity.ok(usuarios);
    }

    /**
     * Get PDF report - requires authentication and admin role
     */
    @GetMapping("/pdf")
    public ResponseEntity<?> descargarPdfUsuarios(@RequestHeader(value = "X-Auth-Token", required = false) String token) {
        Long userId = authenticationService.validateSession(token);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Autenticación requerida.");
        }
        
        if (!authenticationService.isAdmin(token)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Acceso denegado. Se requieren privilegios de administrador.");
        }
        
        List<Usuario> usuarios = usuarioService.getAllUsuarios();
        ByteArrayInputStream bis = pdfService.generarReporteUsuarios(usuarios);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=reporte_usuarios.pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(bis));
    }

    /**
     * Get users by role - requires authentication
     */
    @GetMapping("/rol/{idRol}")
    public ResponseEntity<?> obtenerUsuariosPorRol(
            @PathVariable("idRol") Long idRol,
            @RequestHeader(value = "X-Auth-Token", required = false) String token) {
        Long userId = authenticationService.validateSession(token);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Autenticación requerida.");
        }
        
        List<Usuario> usuarios = usuarioService.obtenerPorRol(idRol);
        // Clear passwords before returning
        usuarios.forEach(u -> u.setContrasenia(null));
        return ResponseEntity.ok(usuarios);
    }

    /**
     * Get paginated users - requires authentication and admin role
     */
    @GetMapping("/paginado")
    public ResponseEntity<?> getUsuariosPaginados(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size,
            @RequestHeader(value = "X-Auth-Token", required = false) String token) {
        
        Long userId = authenticationService.validateSession(token);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Autenticación requerida.");
        }
        
        if (!authenticationService.isAdmin(token)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Acceso denegado. Se requieren privilegios de administrador.");
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<Usuario> usuarios = usuarioService.getUsuariosPaginados(pageable);
        // Clear passwords before returning
        usuarios.forEach(u -> u.setContrasenia(null));
        return ResponseEntity.ok(usuarios);
    }

    /**
     * Get user by ID - requires authentication (own profile or admin)
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getUsuarioById(
            @PathVariable Long id,
            @RequestHeader(value = "X-Auth-Token", required = false) String token) {
        Long userId = authenticationService.validateSession(token);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Autenticación requerida.");
        }
        
        // Users can only view their own profile unless they're admin
        if (!userId.equals(id) && !authenticationService.isAdmin(token)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Acceso denegado.");
        }
        
        Usuario usuario = usuarioService.getUsuarioById(id);
        if (usuario != null) {
            usuario.setContrasenia(null); // Don't return password
            return ResponseEntity.ok(usuario);
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * Create user - requires admin role
     */
    @PostMapping
    public ResponseEntity<?> CreateUsuario(
            @RequestBody UsuarioCreateDTO usuarioDTO,
            @RequestHeader(value = "X-Auth-Token", required = false) String token) {
        Long userId = authenticationService.validateSession(token);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Autenticación requerida.");
        }
        
        if (!authenticationService.isAdmin(token)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Acceso denegado. Se requieren privilegios de administrador.");
        }
        
        try {
            Usuario usuario = usuarioService.createUsuario(usuarioDTO);
            usuario.setContrasenia(null); // Don't return password
            return ResponseEntity.status(HttpStatus.CREATED).body(usuario);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al crear el usuario.");
        }
    }

    /**
     * Update user profile - users can update their own profile (non-sensitive fields)
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUsuario(
            @PathVariable Long id,
            @RequestBody UsuarioUpdateDTO usuarioDTO,
            @RequestHeader(value = "X-Auth-Token", required = false) String token) {
        Long userId = authenticationService.validateSession(token);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Autenticación requerida.");
        }
        
        // Users can only update their own profile
        if (!userId.equals(id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("No puedes modificar el perfil de otro usuario.");
        }
        
        Usuario usuario = usuarioService.updateUsuarioProfile(id, usuarioDTO);
        if (usuario != null) {
            usuario.setContrasenia(null); // Don't return password
            return ResponseEntity.ok(usuario);
        }
        return ResponseEntity.notFound().build();
    }
    
    /**
     * Admin update user - can modify all fields including sensitive ones
     */
    @PutMapping("/admin/{id}")
    public ResponseEntity<?> adminUpdateUsuario(
            @PathVariable Long id,
            @RequestBody AdminUsuarioUpdateDTO usuarioDTO,
            @RequestHeader(value = "X-Auth-Token", required = false) String token) {
        Long userId = authenticationService.validateSession(token);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Autenticación requerida.");
        }
        
        if (!authenticationService.isAdmin(token)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Acceso denegado. Se requieren privilegios de administrador.");
        }
        
        Usuario usuario = usuarioService.adminUpdateUsuario(id, usuarioDTO);
        if (usuario != null) {
            usuario.setContrasenia(null); // Don't return password
            return ResponseEntity.ok(usuario);
        }
        return ResponseEntity.notFound().build();
    }
    
    /**
     * Change password - users can change their own password
     */
    @PostMapping("/{id}/change-password")
    public ResponseEntity<?> changePassword(
            @PathVariable Long id,
            @RequestBody PasswordChangeDTO passwordDTO,
            @RequestHeader(value = "X-Auth-Token", required = false) String token) {
        Long userId = authenticationService.validateSession(token);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Autenticación requerida.");
        }
        
        // Users can only change their own password
        if (!userId.equals(id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("No puedes cambiar la contraseña de otro usuario.");
        }
        
        boolean success = usuarioService.changePassword(id, 
                passwordDTO.getCurrentPassword(), 
                passwordDTO.getNewPassword());
        
        if (success) {
            return ResponseEntity.ok("Contraseña actualizada exitosamente.");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Contraseña actual incorrecta.");
        }
    }
    
    /**
     * Admin password reset - admin can reset any user's password
     */
    @PostMapping("/admin/{id}/reset-password")
    public ResponseEntity<?> adminResetPassword(
            @PathVariable Long id,
            @RequestBody Map<String, String> request,
            @RequestHeader(value = "X-Auth-Token", required = false) String token) {
        Long userId = authenticationService.validateSession(token);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Autenticación requerida.");
        }
        
        if (!authenticationService.isAdmin(token)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Acceso denegado. Se requieren privilegios de administrador.");
        }
        
        String newPassword = request.get("newPassword");
        if (newPassword == null || newPassword.isEmpty()) {
            return ResponseEntity.badRequest().body("Nueva contraseña requerida.");
        }
        
        boolean success = usuarioService.adminResetPassword(id, newPassword);
        
        if (success) {
            return ResponseEntity.ok("Contraseña restablecida exitosamente.");
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Delete user - requires admin role
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUsuario(
            @PathVariable Long id,
            @RequestHeader(value = "X-Auth-Token", required = false) String token) {
        Long userId = authenticationService.validateSession(token);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Autenticación requerida.");
        }
        
        if (!authenticationService.isAdmin(token)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Acceso denegado. Se requieren privilegios de administrador.");
        }
        
        usuarioService.deleteUsuario(id);
        return ResponseEntity.ok("Usuario eliminado exitosamente.");
    }
}