package com.DJuanCarlosPeluqueria.DJuanCarlosPeluqueria.Controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Map;
import com.DJuanCarlosPeluqueria.DJuanCarlosPeluqueria.Model.UsuarioService;
import com.DJuanCarlosPeluqueria.DJuanCarlosPeluqueria.Model.Usuario;
import com.DJuanCarlosPeluqueria.DJuanCarlosPeluqueria.Model.PdfService;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "*")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;
    @Autowired
    private PdfService pdfService;

    // Helper method to get authenticated user ID
    private Long getAuthenticatedUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof Long) {
            return (Long) authentication.getPrincipal();
        }
        return null;
    }

    // Helper method to check if user has admin role (role ID 1 or 2)
    private boolean isAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getAuthorities() != null) {
            return authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_1") || auth.getAuthority().equals("ROLE_2"));
        }
        return false;
    }

    @GetMapping
    public ResponseEntity<?> getallUsuario() {
        // Only admins can list all users
        if (!isAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body("No tiene permisos para listar todos los usuarios.");
        }
        return ResponseEntity.ok(usuarioService.getAllUsuarios());
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials, HttpServletRequest request) {
        try {
            String numDocumentoStr = credentials.get("numDocumento");
            String contrasenia = credentials.get("contrasenia");

            if (numDocumentoStr == null || contrasenia == null) {
                return ResponseEntity.badRequest().body("Por favor ingresa documento y contraseña.");
            }

            Long numDocumento = Long.parseLong(numDocumentoStr);

            // Buscamos al usuario por su número de documento
            Usuario usuario = usuarioService.obtenerPorNumDocumento(numDocumento);

            if (usuario != null && usuario.getContrasenia().equals(contrasenia)) {
                // Establish authenticated session
                HttpSession session = request.getSession(true);
                session.setAttribute("userId", usuario.getIdUsuario());
                session.setAttribute("userRole", usuario.getIdRolFK());
                session.setMaxInactiveInterval(3600); // 1 hour session timeout
                
                // Opcional: limpiar la contraseña antes de responder por seguridad
                usuario.setContrasenia(null);
                return ResponseEntity.ok(usuario);
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
    @GetMapping("/pdf")
    public ResponseEntity<?> descargarPdfUsuarios() {
        // Only admins can generate user reports
        if (!isAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body("No tiene permisos para generar reportes de usuarios.");
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

    @GetMapping("/rol/{idRol}")
    public ResponseEntity<?> obtenerUsuariosPorRol(@PathVariable("idRol") Long idRol) {
        // Only admins can query users by role
        if (!isAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body("No tiene permisos para consultar usuarios por rol.");
        }
        List<Usuario> estilistas = usuarioService.obtenerPorRol(idRol);
        return ResponseEntity.ok(estilistas);
    }

    @GetMapping("/paginado")
    public ResponseEntity<?> getUsuariosPaginados(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size) {
        
        // Only admins can list paginated users
        if (!isAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body("No tiene permisos para listar usuarios.");
        }

        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(usuarioService.getUsuariosPaginados(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUsuarioById(@PathVariable Long id) {
        Long authenticatedUserId = getAuthenticatedUserId();
        
        // Users can only view their own profile unless they are admin
        if (!isAdmin() && !id.equals(authenticatedUserId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body("No tiene permisos para ver este usuario.");
        }
        
        Usuario usuario = usuarioService.getUsuarioById(id);
        if (usuario == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(usuario);
    }

    @PostMapping
    public ResponseEntity<?> CreateUsuario(@RequestBody Usuario usuario) {
        // Only admins can create users with elevated roles
        if (usuario.getIdRolFK() != 3 && !isAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body("No tiene permisos para crear usuarios con roles administrativos.");
        }
        
        return ResponseEntity.ok(usuarioService.createUsuario(usuario));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUsuario(@PathVariable Long id, @RequestBody Usuario usuario) {
        Long authenticatedUserId = getAuthenticatedUserId();
        
        // Users can only update their own profile unless they are admin
        if (!isAdmin() && !id.equals(authenticatedUserId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body("No tiene permisos para modificar este usuario.");
        }
        
        // Non-admin users cannot change their role
        if (!isAdmin()) {
            Usuario existingUser = usuarioService.getUsuarioById(id);
            if (existingUser != null && usuario.getIdRolFK() != existingUser.getIdRolFK()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("No tiene permisos para cambiar el rol de usuario.");
            }
        }
        
        return ResponseEntity.ok(usuarioService.updateUsuario(id, usuario));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUsuario(@PathVariable Long id) {
        // Only admins can delete users
        if (!isAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body("No tiene permisos para eliminar usuarios.");
        }
        
        usuarioService.deleteUsuario(id);
        return ResponseEntity.noContent().build();
    }
}