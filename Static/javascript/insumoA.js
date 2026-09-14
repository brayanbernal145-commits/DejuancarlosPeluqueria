const API_URL = "http://localhost:8080/api/insumos";

// Obtener el ID enviado desde la lista (?id=X)
const urlParams = new URLSearchParams(window.location.search);
const insumoId = urlParams.get('id');

document.addEventListener("DOMContentLoaded", () => {
    if (!insumoId) {
        alert("No se seleccionó ningún insumo para editar.");
        window.location.href = "listadoI.html";
        return;
    }

    cargarDatosInsumo(insumoId);

    const formInsumo = document.getElementById("insumoForm");
    if (formInsumo) {
        formInsumo.addEventListener("submit", actualizarInsumo);
    }
});

// Cargar datos actuales desde Spring Boot
async function cargarDatosInsumo(id) {
    try {
        const response = await fetch(`${API_URL}/${id}`);
        if (!response.ok) throw new Error("Error al obtener los datos del servidor.");

        const insumo = await response.json();

        document.getElementById("nombreInsumo").value = insumo.nombreInsumo || '';
        document.getElementById("descripcionInsumo").value = insumo.descripcionInsumo || '';
        document.getElementById("categoriaInsumo").value = insumo.categoriaInsumo || '';
        document.getElementById("fechaVencimiento").value = insumo.fechaVencimiento || '';
        document.getElementById("stockActual").value = insumo.stockActual || 0;
        document.getElementById("horaActualizacion").value = insumo.horaActualizacion || '';
    } catch (error) {
        console.error("Error:", error);
        alert("No se pudieron cargar los datos del insumo.");
    }
}

// Petición PUT para enviar los cambios
async function actualizarInsumo(event) {
    event.preventDefault();

    const horaInput = document.getElementById("horaActualizacion").value;

    const insumoData = {
        idInsumo: Number(insumoId),
        nombreInsumo: document.getElementById("nombreInsumo").value.trim(),
        descripcionInsumo: document.getElementById("descripcionInsumo").value.trim(),
        categoriaInsumo: document.getElementById("categoriaInsumo").value.trim(),
        fechaVencimiento: document.getElementById("fechaVencimiento").value,
        stockActual: Number(document.getElementById("stockActual").value),
        stockMinPosible: 5,
        horaActualizacion: horaInput.length === 5 ? horaInput + ":00" : horaInput
    };

    try {
        const response = await fetch(`${API_URL}/${insumoId}`, {
            method: "PUT",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(insumoData)
        });

        if (response.ok) {
            alert("¡Insumo actualizado con éxito!");
            window.location.href = "listadoI.html";
        } else {
            const errorMsg = await response.text();
            alert("No se pudo actualizar el insumo: " + errorMsg);
        }
    } catch (error) {
        console.error("Error:", error);
        alert("Error de conexión al intentar actualizar.");
    }
}