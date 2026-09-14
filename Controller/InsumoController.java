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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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


    @GetMapping("/pdf")
    public ResponseEntity<InputStreamResource> descargarPdfInsumos() {
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
    public List<Insumo> getAllInsumos() {
        return insumoService.getAllInsumos();
    }

    @GetMapping("/paginado")
    public ResponseEntity<Map<String, Object>> getInsumosPaginados(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size,
            @RequestParam(required = false, defaultValue = "TODAS") String categoria,
            @RequestParam(required = false, defaultValue = "TODOS") String estado) {

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
    public Insumo getInsumoById(@PathVariable Long id) {
        return insumoService.getInsumoById(id);
    }

    @PostMapping
    public Insumo createInsumo(@RequestBody Insumo insumo) {
        return insumoService.createInsumo(insumo);
    }

    @PutMapping("/{id}")
    public Insumo updateInsumo(@PathVariable Long id, @RequestBody Insumo insumo) {
        return insumoService.updateInsumo(id, insumo);
    }

    @DeleteMapping("/{id}")
    public void deleteInsumo(@PathVariable Long id) {
        insumoService.deleteInsumo(id);
    }
}