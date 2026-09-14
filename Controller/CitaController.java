package com.DJuanCarlosPeluqueria.DJuanCarlosPeluqueria.Controller;

import com.DJuanCarlosPeluqueria.DJuanCarlosPeluqueria.Model.Cita;
import com.DJuanCarlosPeluqueria.DJuanCarlosPeluqueria.Model.CitaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import com.DJuanCarlosPeluqueria.DJuanCarlosPeluqueria.Model.PdfService;
import com.DJuanCarlosPeluqueria.DJuanCarlosPeluqueria.Security.SessionHelper;

import jakarta.servlet.http.HttpServletRequest;
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

    @GetMapping
    public ResponseEntity<?> getAllCitas(HttpServletRequest request) {
        // Only admins and employees can view all appointments
        if (!SessionHelper.isAdmin(request) && !SessionHelper.isEmployee(request)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Access denied. Admin or Employee role required.");
        }
        return ResponseEntity.ok(citaService.getAllCitas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCitaById(@PathVariable Long id, HttpServletRequest request) {
        Cita cita = citaService.getCitaById(id);
        if (cita == null) {
            return ResponseEntity.notFound().build();
        }
        
        // Users can only view their own appointments unless they are admin/employee
        Long authenticatedUserId = SessionHelper.getAuthenticatedUserId(request);
        boolean isAdminOrEmployee = SessionHelper.isAdmin(request) || SessionHelper.isEmployee(request);
        
        if (!isAdminOrEmployee && !cita.getIdUsuarioFK().equals(authenticatedUserId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Access denied. You can only view your own appointments.");
        }
        
        return ResponseEntity.ok(cita);
    }

    @GetMapping("/pdf")
    public ResponseEntity<?> generarReporteCitas(HttpServletRequest request) {
        // Only admins and employees can generate reports
        if (!SessionHelper.isAdmin(request) && !SessionHelper.isEmployee(request)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Access denied. Admin or Employee role required.");
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
    public ResponseEntity<?> obtenerCitasPorUsuario(@PathVariable Long idUsuario, HttpServletRequest request) {
        Long authenticatedUserId = SessionHelper.getAuthenticatedUserId(request);
        boolean isAdminOrEmployee = SessionHelper.isAdmin(request) || SessionHelper.isEmployee(request);
        
        // Users can only view their own appointments unless they are admin/employee
        if (!isAdminOrEmployee && !authenticatedUserId.equals(idUsuario)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Access denied. You can only view your own appointments.");
        }
        
        List<Cita> citas = citaService.obtenerPorIdUsuario(idUsuario.intValue());
        return ResponseEntity.ok(citas);
    }

    @PostMapping
    public ResponseEntity<?> createCita(@RequestBody Cita cita, HttpServletRequest request) {
        // Authenticated users can create appointments
        // But they can only create appointments for themselves unless they are admin/employee
        Long authenticatedUserId = SessionHelper.getAuthenticatedUserId(request);
        boolean isAdminOrEmployee = SessionHelper.isAdmin(request) || SessionHelper.isEmployee(request);
        
        if (!isAdminOrEmployee && !authenticatedUserId.equals(cita.getIdUsuarioFK())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Access denied. You can only create appointments for yourself.");
        }
        
        return ResponseEntity.status(HttpStatus.CREATED).body(citaService.createCita(cita));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCita(@PathVariable Long id, @RequestBody Cita cita, HttpServletRequest request) {
        Cita existingCita = citaService.getCitaById(id);
        if (existingCita == null) {
            return ResponseEntity.notFound().build();
        }
        
        // Users can only update their own appointments unless they are admin/employee
        Long authenticatedUserId = SessionHelper.getAuthenticatedUserId(request);
        boolean isAdminOrEmployee = SessionHelper.isAdmin(request) || SessionHelper.isEmployee(request);
        
        if (!isAdminOrEmployee && !existingCita.getIdUsuarioFK().equals(authenticatedUserId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Access denied. You can only update your own appointments.");
        }
        
        Cita updatedCita = citaService.updateCita(id, cita);
        if (updatedCita != null) {
            return ResponseEntity.ok(updatedCita);
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCita(@PathVariable Long id, HttpServletRequest request) {
        Cita existingCita = citaService.getCitaById(id);
        if (existingCita == null) {
            return ResponseEntity.notFound().build();
        }
        
        // Users can only delete their own appointments unless they are admin/employee
        Long authenticatedUserId = SessionHelper.getAuthenticatedUserId(request);
        boolean isAdminOrEmployee = SessionHelper.isAdmin(request) || SessionHelper.isEmployee(request);
        
        if (!isAdminOrEmployee && !existingCita.getIdUsuarioFK().equals(authenticatedUserId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Access denied. You can only delete your own appointments.");
        }
        
        citaService.deleteCita(id);
        return ResponseEntity.noContent().build();
    }
}