const API_CITAS_URL = "http://localhost:8080/api/citas";
const API_USUARIOS_URL = "http://localhost:8080/api/usuarios";
const API_SERVICIOS_URL = "http://localhost:8080/api/servicios";

document.addEventListener("DOMContentLoaded", () => {
    cargarUsuariosDataList();
    cargarServiciosDataList();
    inicializarVistaPreviaCita();
    cargarCitasRecientes();
    inicializarInstrucciones();

    const citaForm = document.getElementById("citaForm");
    if (citaForm) {
        citaForm.addEventListener("submit", crearCita);
    }
});

/* Mapeo de campos autocompletables para obtener los IDs */
async function cargarUsuariosDataList() {
    try {
        const res = await fetch(API_USUARIOS_URL);
        const data = await res.json();
        const datalist = document.getElementById("listaUsuarios");
        datalist.innerHTML = "";
        data.forEach(u => {
            const opt = document.createElement("option");
            opt.setAttribute("data-id", u.idUsuario);
            opt.value = u.nombreCompleto;
            datalist.appendChild(opt);
        });
    } catch (e) {
        console.error("Error al cargar usuarios:", e);
    }
}

async function cargarServiciosDataList() {
    try {
        const res = await fetch(API_SERVICIOS_URL);
        const data = await res.json();
        const datalist = document.getElementById("listaServicios");
        datalist.innerHTML = "";
        data.forEach(s => {
            const opt = document.createElement("option");
            opt.setAttribute("data-id", s.idServicio);
            opt.value = s.nombreServicio;
            datalist.appendChild(opt);
        });
    } catch (e) {
        console.error("Error al cargar servicios:", e);
    }
}

/* Guardar Cita */
async function crearCita(event) {
    event.preventDefault();

    const inputUser = document.getElementById("buscarUsuario").value;
    const inputServ = document.getElementById("buscarServicio").value;

    const optUser = Array.from(document.querySelectorAll("#listaUsuarios option")).find(o => o.value === inputUser);
    const optServ = Array.from(document.querySelectorAll("#listaServicios option")).find(o => o.value === inputServ);

    if (!optUser || !optServ) {
        alert("Por favor seleccione un cliente y un servicio válidos de la lista.");
        return;
    }

    const citaData = {
        idUsuarioFK: parseInt(optUser.getAttribute("data-id")),
        idServicioFK: parseInt(optServ.getAttribute("data-id")),
        fecha: document.getElementById("fecha").value,
        horaInicio: document.getElementById("horaInicio").value,
        horaFinal: document.getElementById("horaFinal").value,
        estado: document.getElementById("estado").value
    };

    try {
        const response = await fetch(API_CITAS_URL, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(citaData)
        });

        if (response.ok) {
            alert("¡Cita agendada correctamente!");
            document.getElementById("citaForm").reset();
            resetearVistaPreviaCita();
            cargarCitasRecientes();
        } else {
            alert("Error al agendar la cita");
        }
    } catch (e) {
        console.error(e);
        alert("Error de conexión con el backend.");
    }
}

/* Vista Previa Interactiva de Citas */
function inicializarVistaPreviaCita() {
    const inUsuario = document.getElementById("buscarUsuario");
    const inServicio = document.getElementById("buscarServicio");
    const inFecha = document.getElementById("fecha");
    const inHora = document.getElementById("horaInicio");
    const inEstado = document.getElementById("estado");
    const btnLimpiar = document.getElementById("btnLimpiarCita");

    inUsuario?.addEventListener("input", (e) => {
        document.getElementById("prevClienteC").textContent = e.target.value.trim() || "Cliente No Seleccionado";
    });

    inServicio?.addEventListener("input", (e) => {
        document.getElementById("prevServicioC").textContent = e.target.value.trim() ? `Servicio: ${e.target.value}` : "Servicio: -";
    });

    function actualizarHorario() {
        const f = inFecha.value || "--/--";
        const h = inHora.value || "--:--";
        document.getElementById("prevHorarioC").textContent = `Fecha/Hora: ${f} ${h}`;
    }

    inFecha?.addEventListener("change", actualizarHorario);
    inHora?.addEventListener("input", actualizarHorario);

    inEstado?.addEventListener("change", (e) => {
        document.getElementById("prevEstadoC").textContent = e.target.value;
    });

    btnLimpiar?.addEventListener("click", () => setTimeout(resetearVistaPreviaCita, 50));
}

function resetearVistaPreviaCita() {
    document.getElementById("prevClienteC").textContent = "Cliente No Seleccionado";
    document.getElementById("prevServicioC").textContent = "Servicio: -";
    document.getElementById("prevHorarioC").textContent = "Fecha/Hora: --/-- --:--";
    document.getElementById("prevEstadoC").textContent = "Pendiente";
}

/* Cargar últimas citas registradas */
async function cargarCitasRecientes() {
    const lista = document.getElementById("listaUltimasCitas");
    if (!lista) return;

    try {
        const response = await fetch(API_CITAS_URL);
        if (!response.ok) throw new Error("Error al obtener citas");

        const citas = await response.json();
        lista.innerHTML = "";

        if (!citas || citas.length === 0) {
            lista.innerHTML = `<li class="sin-registros">No hay citas registradas.</li>`;
            return;
        }

        const ultimas = citas.slice(-3).reverse();
        ultimas.forEach(c => {
            const li = document.createElement("li");
            li.innerHTML = `
                <div>
                    <strong>Cita #${c.idCita || ''}</strong>
                    <br><small style="color:#aaa">${c.fecha || ''} ${c.horaInicio || ''}</small>
                </div>
                <span>${c.estado || 'Pendiente'}</span>
            `;
            lista.appendChild(li);
        });
    } catch (error) {
        console.error("Error al cargar citas recientes:", error);
        lista.innerHTML = `<li class="sin-registros">No se pudo cargar la lista.</li>`;
    }
}

function inicializarInstrucciones() {
    const btnInstrucciones = document.getElementById("btnInstrucciones");
    const contenidoInstrucciones = document.getElementById("contenidoInstrucciones");
    const iconoFlecha = document.getElementById("iconoFlecha");

    btnInstrucciones?.addEventListener("click", () => {
        contenidoInstrucciones.classList.toggle("abierto");
        iconoFlecha?.classList.toggle("rotar");
    });
}

const menusItemsDropDown = document.querySelectorAll('.menu-item-dropdown');
const sidebar = document.getElementById('sidebar');
const menuBtn = document.getElementById('menu-btn');
const sidebarBtn = document.getElementById('sidebar-btn');

sidebarBtn?.addEventListener('click', () => {
    document.body.classList.toggle('sidebar-hidden');
});

menuBtn?.addEventListener('click', () => {
    sidebar.classList.toggle('minimize');
});

menusItemsDropDown.forEach((menuItem) => {
    menuItem.addEventListener('click', () => {
        const subMenu = menuItem.querySelector('.sub-menu');
        const isActive = menuItem.classList.toggle('sub-menu-toggle');
        if (subMenu) {
            subMenu.style.height = isActive ? `${subMenu.scrollHeight + 20}px` : '0';
            subMenu.style.padding = isActive ? '0.2rem 0' : '0';
        }
    });
});