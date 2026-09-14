package com.DJuanCarlosPeluqueria.DJuanCarlosPeluqueria.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class VistaControlador {

    // --- index de la pagina principal ---

    @GetMapping("/login")
    public String mostrarLogin() {
        return "Login";
    }

    @GetMapping("/index")
    public String mostrarindex() {
        return "index2";
    }

    @GetMapping("/ServiciosIn")
    public String mostrarindexS() {
        return "servicios";
    }
    @GetMapping("/Ubicacion")
    public String mostrarubicacion() {
        return "ubicacion";
    }

    @GetMapping("/estilistas")
    public String mostrarestilistas() {
        return "estilistas";
    }


    // --- AUTENTICACIÓN Y PERFIL ---

    @GetMapping("/registro")
    public String mostrarRegistro() {
        return "registro";
    }

    @GetMapping("/Admin")
    public String mostrarAdmin() {
        return "Admin";
    }

    @GetMapping("/Empleado")
    public String mostrarEmpleado() {
        return "Empleado";
    }

    @GetMapping("/Cliente")
    public String mostrarCliente() {
        return "Cliente";
    }


    // --- MÓDULO DE USUARIOS ---

    @GetMapping("/usuarios")
    public String mostrarListadoU() {
        return "ListadoU";
    }

    @GetMapping("/usuarios/crear")
    public String mostrarCrearU() {
        return "crearU";
    }

    @GetMapping("/usuarios/actualizar")
    public String mostrarActualizarU() {
        return "ActualizarU";
    }

    // --- MÓDULO DE SERVICIOS ---

    @GetMapping("/servicios")
    public String mostrarListadoS() {
        return "ListadoS";
    }

    @GetMapping("/servicios/crear")
    public String mostrarCrearS() {
        return "crearS";
    }

    @GetMapping("/servicios/actualizar")
    public String mostrarActualizarS() {
        return "ActualizarS";
    }

    // --- MÓDULO DE CITAS ---

    @GetMapping("/citas")
    public String mostrarListadoC() {
        return "listadoC";
    }

    @GetMapping("/citas/crear")
    public String mostrarFormularioCita() {
        return "FormularioCita";
    }

    @GetMapping("/citas/editar")
    public String mostrarEditarC() {
        return "editarC";
    }

    // --- MÓDULO DE INSUMOS ---

    @GetMapping("/insumos")
    public String mostrarListadoI() {
        return "listadoI";
    }

    @GetMapping("/insumos/crear")
    public String mostrarFormularioInsumo() {
        return "FormularioInsumo";
    }

    @GetMapping("/insumos/editar")
    public String mostrarEditarI() {
        return "editarl";
    }
}