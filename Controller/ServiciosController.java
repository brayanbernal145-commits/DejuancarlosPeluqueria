package com.DJuanCarlosPeluqueria.DJuanCarlosPeluqueria.Controller;

import com.DJuanCarlosPeluqueria.DJuanCarlosPeluqueria.Model.PdfService;
import com.DJuanCarlosPeluqueria.DJuanCarlosPeluqueria.Security.SessionHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
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

    @GetMapping("/pdf")
    public ResponseEntity<?> generarReporteServicios(HttpServletRequest request) {
        // Only admins and employees can generate reports
        if (!SessionHelper.isAdmin(request) && !SessionHelper.isEmployee(request)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Access denied. Admin or Employee role required.");
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
    public ResponseEntity<?> getAllServicios(HttpServletRequest request) {
        // Only admins and employees can view all services
        if (!SessionHelper.isAdmin(request) && !SessionHelper.isEmployee(request)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Access denied. Admin or Employee role required.");
        }
        return ResponseEntity.ok(servicioService.getAllservicios());
    }

    @GetMapping("/paginado")
    public ResponseEntity<?> obtenerServiciosPaginados(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size,
            @RequestParam(required = false, defaultValue = "") String categoria,
            @RequestParam(required = false, defaultValue = "") String estado,
            HttpServletRequest request) {

        // Only admins and employees can view services
        if (!SessionHelper.isAdmin(request) && !SessionHelper.isEmployee(request)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Access denied. Admin or Employee role required.");
        }

        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(servicioService.buscarConFiltros(categoria, estado, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getServicioById(@PathVariable Long id, HttpServletRequest request) {
        // Only admins and employees can view services
        if (!SessionHelper.isAdmin(request) && !SessionHelper.isEmployee(request)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Access denied. Admin or Employee role required.");
        }
        
        Servicios servicio = servicioService.getServicioById(id);
        if (servicio == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(servicio);
    }

    @PostMapping
    public ResponseEntity<?> createServicio(@RequestBody Servicios servicio, HttpServletRequest request) {
        // Only admins and employees can create services
        if (!SessionHelper.isAdmin(request) && !SessionHelper.isEmployee(request)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Access denied. Admin or Employee role required.");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(servicioService.createServicio(servicio));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateServicio(@PathVariable Long id, @RequestBody Servicios servicio, HttpServletRequest request) {
        // Only admins and employees can update services
        if (!SessionHelper.isAdmin(request) && !SessionHelper.isEmployee(request)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Access denied. Admin or Employee role required.");
        }
        
        Servicios updated = servicioService.updateServicio(id, servicio);
        if (updated == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteServicio(@PathVariable Long id, HttpServletRequest request) {
        // Only admins can delete services
        if (!SessionHelper.isAdmin(request)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Access denied. Admin role required.");
        }
        
        servicioService.deleteServicio(id);
        return ResponseEntity.noContent().build();
    }
}