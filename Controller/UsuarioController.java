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

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Map;
import com.DJuanCarlosPeluqueria.DJuanCarlosPeluqueria.Model.UsuarioService;
import com.DJuanCarlosPeluqueria.DJuanCarlosPeluqueria.Model.Usuario;
import com.DJuanCarlosPeluqueria.DJuanCarlosPeluqueria.Model.PdfService;
import com.DJuanCarlosPeluqueria.DJuanCarlosPeluqueria.Security.SessionHelper;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "*")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;
    @Autowired
    private PdfService pdfService;

    @GetMapping
    public ResponseEntity<?> getallUsuario(HttpServletRequest request) {
        // Only admins and employees can list all users
        if (!SessionHelper.isAdmin(request) && !SessionHelper.isEmployee(request)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Access denied. Admin or Employee role required.");
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
                SessionHelper.setAuthenticatedUser(session, usuario.getIdUsuario(), 
                                                   usuario.getIdRolFK(), usuario.getCargo());
                
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
    
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            SessionHelper.clearAuthentication(session);
        }
        return ResponseEntity.ok().body("Logged out successfully.");
    }
    @GetMapping("/pdf")
    public ResponseEntity<?> descargarPdfUsuarios(HttpServletRequest request) {
        // Only admins and employees can generate user reports
        if (!SessionHelper.isAdmin(request) && !SessionHelper.isEmployee(request)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Access denied. Admin or Employee role required.");
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
    public ResponseEntity<?> obtenerUsuariosPorRol(@PathVariable("idRol") Long idRol, HttpServletRequest request) {
        // Only admins and employees can query users by role
        if (!SessionHelper.isAdmin(request) && !SessionHelper.isEmployee(request)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Access denied. Admin or Employee role required.");
        }
        
        List<Usuario> estilistas = usuarioService.obtenerPorRol(idRol);
        return ResponseEntity.ok(estilistas);
    }

    @GetMapping("/paginado")
    public ResponseEntity<?> getUsuariosPaginados(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size,
            HttpServletRequest request) {

        // Only admins and employees can list users
        if (!SessionHelper.isAdmin(request) && !SessionHelper.isEmployee(request)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Access denied. Admin or Employee role required.");
        }

        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(usuarioService.getUsuariosPaginados(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUsuarioById(@PathVariable Long id, HttpServletRequest request) {
        Long authenticatedUserId = SessionHelper.getAuthenticatedUserId(request);
        
        // Users can only view their own profile unless they are admin/employee
        if (!authenticatedUserId.equals(id) && !SessionHelper.isAdmin(request) && !SessionHelper.isEmployee(request)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Access denied. You can only view your own profile.");
        }
        
        Usuario usuario = usuarioService.getUsuarioById(id);
        if (usuario == null) {
            return ResponseEntity.notFound().build();
        }
        
        // Clear password before returning
        usuario.setContrasenia(null);
        return ResponseEntity.ok(usuario);
    }

    @PostMapping
    public ResponseEntity<?> CreateUsuario(@RequestBody Usuario usuario, HttpServletRequest request) {
        // Only admins can create users with elevated privileges
        // Regular users can self-register but only as clients
        boolean isAdmin = SessionHelper.isAdmin(request);
        
        if (!isAdmin) {
            // Force new users to be clients with default role
            usuario.setCargo("Cliente");
            usuario.setIdRolFK(3); // Client role
            usuario.setEstado("Activo");
        }
        
        Usuario createdUsuario = usuarioService.createUsuario(usuario);
        createdUsuario.setContrasenia(null); // Don't return password
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUsuario);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUsuario(@PathVariable Long id, @RequestBody Usuario usuario, HttpServletRequest request) {
        Long authenticatedUserId = SessionHelper.getAuthenticatedUserId(request);
        boolean isAdmin = SessionHelper.isAdmin(request);
        
        // Users can only update their own profile unless they are admin
        if (!authenticatedUserId.equals(id) && !isAdmin) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Access denied. You can only update your own profile.");
        }
        
        // Get existing user to preserve sensitive fields
        Usuario existingUsuario = usuarioService.getUsuarioById(id);
        if (existingUsuario == null) {
            return ResponseEntity.notFound().build();
        }
        
        // Non-admin users cannot change their own role, cargo, or estado
        if (!isAdmin) {
            usuario.setIdRolFK(existingUsuario.getIdRolFK());
            usuario.setCargo(existingUsuario.getCargo());
            usuario.setEstado(existingUsuario.getEstado());
        }
        
        Usuario updatedUsuario = usuarioService.updateUsuario(id, usuario);
        if (updatedUsuario != null) {
            updatedUsuario.setContrasenia(null); // Don't return password
            return ResponseEntity.ok(updatedUsuario);
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Failed to update user.");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUsuario(@PathVariable Long id, HttpServletRequest request) {
        // Only admins can delete users
        if (!SessionHelper.isAdmin(request)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Access denied. Admin role required.");
        }
        
        usuarioService.deleteUsuario(id);
        return ResponseEntity.noContent().build();
    }
}