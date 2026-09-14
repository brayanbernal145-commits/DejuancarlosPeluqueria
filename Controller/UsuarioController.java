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

    @GetMapping
    public List<Usuario> getallUsuario() {
        return usuarioService.getAllUsuarios();
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
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
    public ResponseEntity<InputStreamResource> descargarPdfUsuarios() {
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
    public ResponseEntity<List<Usuario>> obtenerUsuariosPorRol(@PathVariable("idRol") Long idRol) {
        List<Usuario> estilistas = usuarioService.obtenerPorRol(idRol);
        return ResponseEntity.ok(estilistas);
    }

    @GetMapping("/paginado")
    public Page<Usuario> getUsuariosPaginados(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size) {

        Pageable pageable = PageRequest.of(page, size);
        return usuarioService.getUsuariosPaginados(pageable);
    }

    @GetMapping("/{id}")
    public Usuario getUsuarioById(@PathVariable Long id) {
        return usuarioService.getUsuarioById(id);
    }

    @PostMapping
    public Usuario CreateUsuario(@RequestBody Usuario usuario) {
        return usuarioService.createUsuario(usuario);
    }

    @PutMapping("/{id}")
    public Usuario updateUsuario(@PathVariable Long id, @RequestBody Usuario usuario) {
        return usuarioService.updateUsuario(id, usuario);
    }

    @DeleteMapping("/{id}")
    public void deleteUsuario(@PathVariable Long id) {
        usuarioService.deleteUsuario(id);
    }
}