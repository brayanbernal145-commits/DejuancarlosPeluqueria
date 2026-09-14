const API_URL = "http://localhost:8080/api/citas";

const urlParams = new URLSearchParams(window.location.search);
const citaId = urlParams.get('id');

document.addEventListener("DOMContentLoaded", () => {
    configurarPredictivo("buscarUsuario", "listaClientes", "idUsuarioFK");
    configurarPredictivo("buscarServicio", "listaServicios", "idServicioFK");

    if (!citaId) {
        alert("No se seleccionó ninguna cita para editar.");
        window.location.href = "listadoC.html";
        return;
    }

    cargarDatosCita(citaId);

    const formCita = document.getElementById("editarCitaForm");
    if (formCita) {
        formCita.addEventListener("submit", actualizarCita);
    }
});

function configurarPredictivo(inputId, listId, hiddenId) {
    const inputElement = document.getElementById(inputId);
    const hiddenElement = document.getElementById(hiddenId);

    if (inputElement && hiddenElement) {
        inputElement.addEventListener("input", function () {
            const listOptions = document.querySelectorAll(`#${listId} option`);
            let match = false;

            listOptions.forEach(option => {
                if (option.value === inputElement.value) {
                    hiddenElement.value = option.getAttribute("data-id");
                    match = true;
                }
            });

            if (!match) hiddenElement.value = "";
        });
    }
}

async function cargarDatosCita(id) {
    try {
        const response = await fetch(`${API_URL}/${id}`);
        if (!response.ok) throw new Error("Error al obtener la cita desde la API.");

        const cita = await response.json();

        document.getElementById("idCita").value = cita.idCita || id;
        document.getElementById("fecha").value = cita.fecha || '';
        document.getElementById("horaInicio").value = cita.horaInicio ? cita.horaInicio.substring(0, 5) : '';
        document.getElementById("horaFinal").value = cita.horaFinal ? cita.horaFinal.substring(0, 5) : '';
        document.getElementById("estado").value = cita.estado || 'Pendiente';
        document.getElementById("totalServicio").value = cita.totalServicio || 0;
        document.getElementById("observacion").value = cita.observacion || '';

        // Mapeo dinámico de Usuario desde el datalist por data-id
        if (cita.idUsuarioFK) {
            document.getElementById("idUsuarioFK").value = cita.idUsuarioFK;
            const userOpt = document.querySelector(`#listaClientes option[data-id="${cita.idUsuarioFK}"]`);
            if (userOpt) {
                document.getElementById("buscarUsuario").value = userOpt.value;
            }
        }

        // Mapeo dinámico de Servicio desde el datalist por data-id o texto directo
        if (cita.idServicioFK) {
            document.getElementById("idServicioFK").value = cita.idServicioFK;
            const servOpt = document.querySelector(`#listaServicios option[data-id="${cita.idServicioFK}"]`);
            if (servOpt) {
                document.getElementById("buscarServicio").value = servOpt.value;
            } else if (cita.servicio) {
                document.getElementById("buscarServicio").value = cita.servicio;
            }
        } else if (cita.servicio) {
            document.getElementById("buscarServicio").value = cita.servicio;
        }

    } catch (error) {
        console.error("Error al cargar la cita:", error);
        alert("No se pudieron cargar los datos de la cita.");
    }
}

async function actualizarCita(event) {
    event.preventDefault();

    const horaInicioInput = document.getElementById("horaInicio").value;
    const horaFinalInput = document.getElementById("horaFinal").value;

    const citaData = {
        idCita: Number(citaId),
        fecha: document.getElementById("fecha").value,
        horaInicio: horaInicioInput.length === 5 ? horaInicioInput + ":00" : horaInicioInput,
        horaFinal: horaFinalInput.length === 5 ? horaFinalInput + ":00" : horaFinalInput,
        estado: document.getElementById("estado").value,
        totalServicio: Number(document.getElementById("totalServicio").value),
        observacion: document.getElementById("observacion").value.trim(),

        // Campo requerido por la tabla de MySQL
        servicio: document.getElementById("buscarServicio").value.trim(),

        idUsuarioFK: Number(document.getElementById("idUsuarioFK").value || 0),
        idServicioFK: Number(document.getElementById("idServicioFK").value || 0)
    };

    try {
        const response = await fetch(`${API_URL}/${citaId}`, {
            method: "PUT",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(citaData)
        });

        if (response.ok) {
            alert("¡Cita actualizada con éxito!");
            window.location.href = "listadoC.html";
        } else {
            const errorMsg = await response.text();
            alert("No se pudo actualizar la cita: " + errorMsg);
        }
    } catch (error) {
        console.error("Error en la petición PUT:", error);
        alert("Error de conexión al intentar actualizar la cita.");
    }
}