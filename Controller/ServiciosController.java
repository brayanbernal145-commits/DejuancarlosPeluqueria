package com.DJuanCarlosPeluqueria.DJuanCarlosPeluqueria.Controller;

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
    public ResponseEntity<InputStreamResource> generarReporteServicios() {
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
        return servicioService.getAllservicios();
    }

    @GetMapping("/paginado")
    public Page<Servicios> obtenerServiciosPaginados(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size,
            @RequestParam(required = false, defaultValue = "") String categoria,
            @RequestParam(required = false, defaultValue = "") String estado) {

        Pageable pageable = PageRequest.of(page, size);
        return servicioService.buscarConFiltros(categoria, estado, pageable);
    }

    @GetMapping("/{id}")
    public Servicios getServicioById(@PathVariable Long id) {
        return servicioService.getServicioById(id);
    }

    @PostMapping
    public Servicios createServicio(@RequestBody Servicios servicio) {
        return servicioService.createServicio(servicio);
    }

    @PutMapping("/{id}")
    public Servicios updateServicio(@PathVariable Long id, @RequestBody Servicios servicio) {
        return servicioService.updateServicio(id, servicio);
    }

    @DeleteMapping("/{id}")
    public void deleteServicio(@PathVariable Long id) {
        servicioService.deleteServicio(id);
    }
}