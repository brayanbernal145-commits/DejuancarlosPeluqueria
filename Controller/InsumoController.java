package com.DJuanCarlosPeluqueria.DJuanCarlosPeluqueria.Controller;

import com.DJuanCarlosPeluqueria.DJuanCarlosPeluqueria.Model.Insumo;
import com.DJuanCarlosPeluqueria.DJuanCarlosPeluqueria.Model.InsumoService;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/insumos")
@CrossOrigin(origins = "*")
public class InsumoController {

    @Autowired
    private InsumoService insumoService;
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
    public ResponseEntity<?> descargarPdfInsumos() {
        // Only admins can generate inventory reports
        if (!isAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body("No tiene permisos para generar reportes de insumos.");
        }
        
        List<Insumo> insumos = insumoService.getAllInsumos();
        ByteArrayInputStream bis = pdfService.generarReporteInsumos(insumos);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=reporte_insumos.pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(bis));
    }

    @GetMapping
    public ResponseEntity<?> getAllInsumos() {
        // Only admins can view all inventory
        if (!isAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body("No tiene permisos para ver el inventario.");
        }
        return ResponseEntity.ok(insumoService.getAllInsumos());
    }

    @GetMapping("/paginado")
    public ResponseEntity<?> getInsumosPaginados(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size,
            @RequestParam(required = false, defaultValue = "TODAS") String categoria,
            @RequestParam(required = false, defaultValue = "TODOS") String estado) {

        // Only admins can view paginated inventory
        if (!isAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body("No tiene permisos para ver el inventario.");
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<Insumo> insumosPage = insumoService.buscarConFiltros(categoria, estado, pageable);

        Map<String, Object> response = new HashMap<>();
        response.put("content", insumosPage.getContent());
        response.put("number", insumosPage.getNumber());
        response.put("totalElements", insumosPage.getTotalElements());
        response.put("totalPages", insumosPage.getTotalPages());
        response.put("numberOfElements", insumosPage.getNumberOfElements());
        response.put("first", insumosPage.isFirst());
        response.put("last", insumosPage.isLast());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getInsumoById(@PathVariable Long id) {
        // Only admins can view inventory items
        if (!isAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body("No tiene permisos para ver este insumo.");
        }
        
        Insumo insumo = insumoService.getInsumoById(id);
        if (insumo == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(insumo);
    }

    @PostMapping
    public ResponseEntity<?> createInsumo(@RequestBody Insumo insumo) {
        // Only admins can create inventory items
        if (!isAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body("No tiene permisos para crear insumos.");
        }
        return ResponseEntity.ok(insumoService.createInsumo(insumo));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateInsumo(@PathVariable Long id, @RequestBody Insumo insumo) {
        // Only admins can update inventory items
        if (!isAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body("No tiene permisos para modificar insumos.");
        }
        return ResponseEntity.ok(insumoService.updateInsumo(id, insumo));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteInsumo(@PathVariable Long id) {
        // Only admins can delete inventory items
        if (!isAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body("No tiene permisos para eliminar insumos.");
        }
        insumoService.deleteInsumo(id);
        return ResponseEntity.noContent().build();
    }
}