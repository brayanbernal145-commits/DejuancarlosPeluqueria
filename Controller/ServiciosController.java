package com.DJuanCarlosPeluqueria.DJuanCarlosPeluqueria.Controller;

import com.DJuanCarlosPeluqueria.DJuanCarlosPeluqueria.Model.PdfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.util.List;

import com.DJuanCarlosPeluqueria.DJuanCarlosPeluqueria.Model.ServiciosService;
import com.DJuanCarlosPeluqueria.DJuanCarlosPeluqueria.Model.Servicios;

@RestController
@RequestMapping("/api/servicios")
@CrossOrigin(origins = "*")
public class ServiciosController {

    @Autowired
    private ServiciosService servicioService;

    @Autowired
    private PdfService pdfService;

    // Helper method to check if user has admin role (role ID 1 or 2)
    private boolean isAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getAuthorities() != null) {
            return authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_1") || auth.getAuthority().equals("ROLE_2"));
        }
        return false;
    }

    @GetMapping("/pdf")
    public ResponseEntity<?> generarReporteServicios() {
        // Only admins can generate service reports
        if (!isAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body("No tiene permisos para generar reportes de servicios.");
        }
        
        List<Servicios> servicios = servicioService.getAllservicios();
        ByteArrayInputStream bis = pdfService.generarReporteServicios(servicios);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=reporte_servicios.pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(bis));
    }

    @GetMapping
    public List<Servicios> getAllServicios() {
        // Services list can be viewed by all authenticated users (needed for booking)
        return servicioService.getAllservicios();
    }

    @GetMapping("/paginado")
    public Page<Servicios> obtenerServiciosPaginados(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size,
            @RequestParam(required = false, defaultValue = "") String categoria,
            @RequestParam(required = false, defaultValue = "") String estado) {

        // Paginated services can be viewed by all authenticated users (needed for booking)
        Pageable pageable = PageRequest.of(page, size);
        return servicioService.buscarConFiltros(categoria, estado, pageable);
    }

    @GetMapping("/{id}")
    public Servicios getServicioById(@PathVariable Long id) {
        // Service details can be viewed by all authenticated users (needed for booking)
        return servicioService.getServicioById(id);
    }

    @PostMapping
    public ResponseEntity<?> createServicio(@RequestBody Servicios servicio) {
        // Only admins can create services
        if (!isAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body("No tiene permisos para crear servicios.");
        }
        return ResponseEntity.ok(servicioService.createServicio(servicio));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateServicio(@PathVariable Long id, @RequestBody Servicios servicio) {
        // Only admins can update services
        if (!isAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body("No tiene permisos para modificar servicios.");
        }
        return ResponseEntity.ok(servicioService.updateServicio(id, servicio));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteServicio(@PathVariable Long id) {
        // Only admins can delete services
        if (!isAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body("No tiene permisos para eliminar servicios.");
        }
        servicioService.deleteServicio(id);
        return ResponseEntity.noContent().build();
    }
}