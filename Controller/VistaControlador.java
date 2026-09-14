package com.DJuanCarlosPeluqueria.DJuanCarlosPeluqueria.Controller;

import com.DJuanCarlosPeluqueria.DJuanCarlosPeluqueria.Security.SessionHelper;
import jakarta.servlet.http.HttpServletRequest;
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
    public String mostrarAdmin(HttpServletRequest request) {
        // Only admins can access admin panel
        if (!SessionHelper.isAdmin(request)) {
            return "redirect:/login";
        }
        return "Admin";
    }

    @GetMapping("/Empleado")
    public String mostrarEmpleado(HttpServletRequest request) {
        // Only employees can access employee panel
        if (!SessionHelper.isEmployee(request)) {
            return "redirect:/login";
        }
        return "Empleado";
    }

    @GetMapping("/Cliente")
    public String mostrarCliente(HttpServletRequest request) {
        // Only authenticated clients can access client panel
        Integer roleId = SessionHelper.getAuthenticatedUserRole(request);
        if (roleId == null || roleId != 3) {
            return "redirect:/login";
        }
        return "Cliente";
    }


    // --- MÓDULO DE USUARIOS ---

    @GetMapping("/usuarios")
    public String mostrarListadoU(HttpServletRequest request) {
        // Only admins and employees can manage users
        if (!SessionHelper.isAdmin(request) && !SessionHelper.isEmployee(request)) {
            return "redirect:/login";
        }
        return "ListadoU";
    }

    @GetMapping("/usuarios/crear")
    public String mostrarCrearU(HttpServletRequest request) {
        // Only admins can create users
        if (!SessionHelper.isAdmin(request)) {
            return "redirect:/login";
        }
        return "crearU";
    }

    @GetMapping("/usuarios/actualizar")
    public String mostrarActualizarU(HttpServletRequest request) {
        // Only admins and employees can update users
        if (!SessionHelper.isAdmin(request) && !SessionHelper.isEmployee(request)) {
            return "redirect:/login";
        }
        return "ActualizarU";
    }

    // --- MÓDULO DE SERVICIOS ---

    @GetMapping("/servicios")
    public String mostrarListadoS(HttpServletRequest request) {
        // Only admins and employees can manage services
        if (!SessionHelper.isAdmin(request) && !SessionHelper.isEmployee(request)) {
            return "redirect:/login";
        }
        return "ListadoS";
    }

    @GetMapping("/servicios/crear")
    public String mostrarCrearS(HttpServletRequest request) {
        // Only admins and employees can create services
        if (!SessionHelper.isAdmin(request) && !SessionHelper.isEmployee(request)) {
            return "redirect:/login";
        }
        return "crearS";
    }

    @GetMapping("/servicios/actualizar")
    public String mostrarActualizarS(HttpServletRequest request) {
        // Only admins and employees can update services
        if (!SessionHelper.isAdmin(request) && !SessionHelper.isEmployee(request)) {
            return "redirect:/login";
        }
        return "ActualizarS";
    }

    // --- MÓDULO DE CITAS ---

    @GetMapping("/citas")
    public String mostrarListadoC(HttpServletRequest request) {
        // Authenticated users can view appointments
        if (SessionHelper.getAuthenticatedUserId(request) == null) {
            return "redirect:/login";
        }
        return "listadoC";
    }

    @GetMapping("/citas/crear")
    public String mostrarFormularioCita(HttpServletRequest request) {
        // Authenticated users can create appointments
        if (SessionHelper.getAuthenticatedUserId(request) == null) {
            return "redirect:/login";
        }
        return "FormularioCita";
    }

    @GetMapping("/citas/editar")
    public String mostrarEditarC(HttpServletRequest request) {
        // Authenticated users can edit appointments
        if (SessionHelper.getAuthenticatedUserId(request) == null) {
            return "redirect:/login";
        }
        return "editarC";
    }

    // --- MÓDULO DE INSUMOS ---

    @GetMapping("/insumos")
    public String mostrarListadoI(HttpServletRequest request) {
        // Only admins and employees can manage inventory
        if (!SessionHelper.isAdmin(request) && !SessionHelper.isEmployee(request)) {
            return "redirect:/login";
        }
        return "listadoI";
    }

    @GetMapping("/insumos/crear")
    public String mostrarFormularioInsumo(HttpServletRequest request) {
        // Only admins and employees can create inventory items
        if (!SessionHelper.isAdmin(request) && !SessionHelper.isEmployee(request)) {
            return "redirect:/login";
        }
        return "FormularioInsumo";
    }

    @GetMapping("/insumos/editar")
    public String mostrarEditarI(HttpServletRequest request) {
        // Only admins and employees can edit inventory items
        if (!SessionHelper.isAdmin(request) && !SessionHelper.isEmployee(request)) {
            return "redirect:/login";
        }
        return "editarl";
    }
}