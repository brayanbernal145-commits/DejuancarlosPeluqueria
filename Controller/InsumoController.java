package com.DJuanCarlosPeluqueria.DJuanCarlosPeluqueria.Controller;

import com.DJuanCarlosPeluqueria.DJuanCarlosPeluqueria.Model.Insumo;
import com.DJuanCarlosPeluqueria.DJuanCarlosPeluqueria.Model.InsumoService;
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


    @GetMapping("/pdf")
    public ResponseEntity<?> descargarPdfInsumos(HttpServletRequest request) {
        // Only admins and employees can generate reports
        if (!SessionHelper.isAdmin(request) && !SessionHelper.isEmployee(request)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Access denied. Admin or Employee role required.");
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
    public ResponseEntity<?> getAllInsumos(HttpServletRequest request) {
        // Only admins and employees can view inventory
        if (!SessionHelper.isAdmin(request) && !SessionHelper.isEmployee(request)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Access denied. Admin or Employee role required.");
        }
        return ResponseEntity.ok(insumoService.getAllInsumos());
    }

    @GetMapping("/paginado")
    public ResponseEntity<?> getInsumosPaginados(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size,
            @RequestParam(required = false, defaultValue = "TODAS") String categoria,
            @RequestParam(required = false, defaultValue = "TODOS") String estado,
            HttpServletRequest request) {

        // Only admins and employees can view inventory
        if (!SessionHelper.isAdmin(request) && !SessionHelper.isEmployee(request)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Access denied. Admin or Employee role required.");
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
    public ResponseEntity<?> getInsumoById(@PathVariable Long id, HttpServletRequest request) {
        // Only admins and employees can view inventory
        if (!SessionHelper.isAdmin(request) && !SessionHelper.isEmployee(request)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Access denied. Admin or Employee role required.");
        }
        
        Insumo insumo = insumoService.getInsumoById(id);
        if (insumo == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(insumo);
    }

    @PostMapping
    public ResponseEntity<?> createInsumo(@RequestBody Insumo insumo, HttpServletRequest request) {
        // Only admins and employees can create inventory items
        if (!SessionHelper.isAdmin(request) && !SessionHelper.isEmployee(request)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Access denied. Admin or Employee role required.");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(insumoService.createInsumo(insumo));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateInsumo(@PathVariable Long id, @RequestBody Insumo insumo, HttpServletRequest request) {
        // Only admins and employees can update inventory items
        if (!SessionHelper.isAdmin(request) && !SessionHelper.isEmployee(request)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Access denied. Admin or Employee role required.");
        }
        
        Insumo updated = insumoService.updateInsumo(id, insumo);
        if (updated == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteInsumo(@PathVariable Long id, HttpServletRequest request) {
        // Only admins can delete inventory items
        if (!SessionHelper.isAdmin(request)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Access denied. Admin role required.");
        }
        
        insumoService.deleteInsumo(id);
        return ResponseEntity.noContent().build();
    }
}