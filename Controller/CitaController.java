package com.DJuanCarlosPeluqueria.DJuanCarlosPeluqueria.Controller;

import com.DJuanCarlosPeluqueria.DJuanCarlosPeluqueria.Model.Cita;
import com.DJuanCarlosPeluqueria.DJuanCarlosPeluqueria.Model.CitaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
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

    @GetMapping
    public List<Cita> getAllCitas() {
        return citaService.getAllCitas();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Cita> getCitaById(@PathVariable Long id) {
        Cita cita = citaService.getCitaById(id);
        if (cita != null) {
            return ResponseEntity.ok(cita);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/pdf")
    public ResponseEntity<InputStreamResource> generarReporteCitas() {
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
    public ResponseEntity<List<Cita>> obtenerCitasPorUsuario(@PathVariable Integer idUsuario) {
        List<Cita> citas = citaService.obtenerPorIdUsuario(idUsuario);
        return ResponseEntity.ok(citas);
    }

    @PostMapping
    public Cita createCita(@RequestBody Cita cita) {
        return citaService.createCita(cita);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Cita> updateCita(@PathVariable Long id, @RequestBody Cita cita) {
        Cita updatedCita = citaService.updateCita(id, cita);
        if (updatedCita != null) {
            return ResponseEntity.ok(updatedCita);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCita(@PathVariable Long id) {
        citaService.deleteCita(id);
        return ResponseEntity.noContent().build();
    }
}