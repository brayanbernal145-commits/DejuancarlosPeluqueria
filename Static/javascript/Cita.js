const API_URL = "http://localhost:8080/api/citas";

document.addEventListener("DOMContentLoaded", () => {
    consultarCitas();
    inicializarSidebar();
});

// GET: Obtener citas de la BD y mostrarlas en la tabla
async function consultarCitas() {
    const tbody = document.getElementById("tablaCitasBody");
    const infoTotal = document.getElementById("infoTotalCitas");

    try {
        const response = await fetch(API_URL);

        if (!response.ok) throw new Error(`Error HTTP: ${response.status}`);

        const citas = await response.json();
        tbody.innerHTML = "";

        if (citas.length === 0) {
            tbody.innerHTML = `
                <tr>
                    <td colspan="10" class="sin-datos">No hay citas registradas en la base de datos.</td>
                </tr>`;
            if (infoTotal) infoTotal.textContent = "Mostrando 0 citas";
            return;
        }

        if (infoTotal) {
            infoTotal.textContent = `Mostrando ${citas.length} citas registradas`;
        }

        // Construir filas
        citas.forEach(cita => {
            const tr = document.createElement("tr");

            tr.innerHTML = `
                <td>#${cita.idCita || cita.id || 'N/A'}</td>
                <td><strong>${cita.nombreCliente || 'Cliente ' + (cita.idUsuarioFK || '')}</strong></td>
                <td>${cita.servicio || 'Servicio ' + (cita.idServicioFK || '')}</td>
                <td>${cita.observacion || 'Sin observaciones'}</td>
                <td>${cita.fecha || '-'}</td>
                <td>${formatearHoraAMPM(cita.horaInicio)}</td>
                <td>${formatearHoraAMPM(cita.horaFinal)}</td>
                <td>$${Number(cita.totalServicio || 0).toLocaleString()}</td>
                <td><span class="badge ${String(cita.estado || 'pendiente').toLowerCase().replace(/\s+/g, '-')}">${cita.estado || 'Pendiente'}</span></td>
                <td>
                    <div class="acciones">
                        <a class="btn-accion editar" href="editarC.html?id=${cita.idCita || cita.id}" title="Editar">
                            <i class='bx bx-edit'></i>
                        </a>
                        <button class="btn-accion eliminar" onclick="eliminarCita(${cita.idCita || cita.id})" title="Eliminar">
                            <i class='bx bx-trash'></i>
                        </button>
                    </div>
                </td>
            `;
            tbody.appendChild(tr);
        });

    } catch (error) {
        console.error("Error al cargar las citas:", error);
        tbody.innerHTML = `
            <tr>
                <td colspan="10" class="sin-datos" style="color: red;">
                    Error al conectar con Spring Boot. Revisa la consola o activa el servidor.
                </td>
            </tr>`;
    }
}

// Auxiliar: Formato 12 horas AM/PM
function formatearHoraAMPM(hora24) {
    if (!hora24) return "--:--";
    const partes = hora24.split(":");
    if (partes.length < 2) return hora24;
    let horas = parseInt(partes[0], 10);
    const m = partes[1];
    const sufijo = horas >= 12 ? "PM" : "AM";
    horas = horas % 12 || 12;
    return `${horas.toString().padStart(2, "0")}:${m} ${sufijo}`;
}

// DELETE: Eliminar cita por ID
async function eliminarCita(id) {
    if (!confirm("¿Está seguro de que desea eliminar esta cita?")) return;

    try {
        const response = await fetch(`${API_URL}/${id}`, { method: "DELETE" });

        if (response.ok) {
            alert("Cita eliminada correctamente de la base de datos.");
            consultarCitas();
        } else {
            alert("No se pudo eliminar la cita.");
        }
    } catch (error) {
        console.error("Error al eliminar:", error);
        alert("Error de conexión al eliminar la cita.");
    }
}

/* Menú Lateral */
function inicializarSidebar() {
    const menusItemsDropDown = document.querySelectorAll('.menu-item-dropdown');
    const sidebar = document.getElementById('sidebar');
    const menuBtn = document.getElementById('menu-btn');
    const sidebarBtn = document.getElementById('sidebar-btn');

    sidebarBtn?.addEventListener('click', () => document.body.classList.toggle('sidebar-hidden'));
    menuBtn?.addEventListener('click', () => sidebar?.classList.toggle('minimize'));

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
}