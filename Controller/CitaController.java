package com.DJuanCarlosPeluqueria.DJuanCarlosPeluqueria.Controller;

import com.DJuanCarlosPeluqueria.DJuanCarlosPeluqueria.Model.Cita;
import com.DJuanCarlosPeluqueria.DJuanCarlosPeluqueria.Model.CitaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import com.DJuanCarlosPeluqueria.DJuanCarlosPeluqueria.Model.PdfService;

import java.io.ByteArrayInputStream;
import java.util.List;



@RestController
@RequestMapping("/api/citas")
@CrossOrigin(origins = "*")

public class CitaController {

    @Autowired
    private CitaService citaService;

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
    public ResponseEntity<?> getAllCitas() {
        Long authenticatedUserId = getAuthenticatedUserId();
        
        // Only admins can view all appointments
        if (!isAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body("No tiene permisos para ver todas las citas. Use /api/citas/usuario/{idUsuario} para ver sus propias citas.");
        }
        
        return ResponseEntity.ok(citaService.getAllCitas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCitaById(@PathVariable Long id) {
        Cita cita = citaService.getCitaById(id);
        if (cita == null) {
            return ResponseEntity.notFound().build();
        }
        
        Long authenticatedUserId = getAuthenticatedUserId();
        
        // Users can only view their own appointments unless they are admin
        if (!isAdmin() && !cita.getIdUsuarioFK().equals(authenticatedUserId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body("No tiene permisos para ver esta cita.");
        }
        
        return ResponseEntity.ok(cita);
    }

    @GetMapping("/pdf")
    public ResponseEntity<?> generarReporteCitas() {
        // Only admins can generate appointment reports
        if (!isAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body("No tiene permisos para generar reportes de citas.");
        }
        
        List<Cita> citas = citaService.getAllCitas();
        ByteArrayInputStream bis = pdfService.generarReporteCitas(citas);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=reporte_citas.pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(bis));
    }
    
    // Obtener todas las citas de un usuario específico
    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<?> obtenerCitasPorUsuario(@PathVariable Long idUsuario) {
        Long authenticatedUserId = getAuthenticatedUserId();
        
        // Users can only view their own appointments unless they are admin
        if (!isAdmin() && !idUsuario.equals(authenticatedUserId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body("No tiene permisos para ver las citas de otro usuario.");
        }
        
        List<Cita> citas = citaService.obtenerPorIdUsuario(idUsuario.intValue());
        return ResponseEntity.ok(citas);
    }

    @PostMapping
    public ResponseEntity<?> createCita(@RequestBody Cita cita) {
        Long authenticatedUserId = getAuthenticatedUserId();
        
        // Users can only create appointments for themselves unless they are admin
        if (!isAdmin() && !cita.getIdUsuarioFK().equals(authenticatedUserId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body("No tiene permisos para crear citas para otro usuario.");
        }
        
        return ResponseEntity.ok(citaService.createCita(cita));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCita(@PathVariable Long id, @RequestBody Cita cita) {
        Cita existingCita = citaService.getCitaById(id);
        if (existingCita == null) {
            return ResponseEntity.notFound().build();
        }
        
        Long authenticatedUserId = getAuthenticatedUserId();
        
        // Users can only update their own appointments unless they are admin
        if (!isAdmin() && !existingCita.getIdUsuarioFK().equals(authenticatedUserId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body("No tiene permisos para modificar esta cita.");
        }
        
        // Non-admin users cannot change appointment ownership
        if (!isAdmin() && cita.getIdUsuarioFK() != null && 
            !cita.getIdUsuarioFK().equals(existingCita.getIdUsuarioFK())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body("No tiene permisos para cambiar el propietario de la cita.");
        }
        
        Cita updatedCita = citaService.updateCita(id, cita);
        return ResponseEntity.ok(updatedCita);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCita(@PathVariable Long id) {
        Cita existingCita = citaService.getCitaById(id);
        if (existingCita == null) {
            return ResponseEntity.notFound().build();
        }
        
        Long authenticatedUserId = getAuthenticatedUserId();
        
        // Users can only delete their own appointments unless they are admin
        if (!isAdmin() && !existingCita.getIdUsuarioFK().equals(authenticatedUserId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body("No tiene permisos para eliminar esta cita.");
        }
        
        citaService.deleteCita(id);
        return ResponseEntity.noContent().build();
    }
}