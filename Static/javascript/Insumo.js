const API_URL = "http://localhost:8080/api/insumos";

let paginaActual = 0;
const elementosPorPagina = 6;
let categoriaActual = 'TODAS';
let estadoActual = 'TODOS';

document.addEventListener("DOMContentLoaded", () => {
    inicializarSidebar();
    cargarInsumos(paginaActual);
});

async function cargarInsumos(pagina) {
    const tbody = document.getElementById("tablaInsumosBody");
    const infoTotal = document.getElementById("infoTotalInsumos");

    try {
        const url = `${API_URL}/paginado?page=${pagina}&size=${elementosPorPagina}&categoria=${encodeURIComponent(categoriaActual)}&estado=${encodeURIComponent(estadoActual)}`;

        const response = await fetch(url);
        if (!response.ok) throw new Error(`Error en el servidor: ${response.status}`);

        const data = await response.json();
        paginaActual = data.number;

        renderizarTabla(data.content);
        renderizarPaginacion(data);

        if (infoTotal) {
            infoTotal.textContent = `Mostrando ${data.numberOfElements} de ${data.totalElements} insumos registrados`;
        }
    } catch (error) {
        console.error("Error al cargar los insumos:", error);
        tbody.innerHTML = `
            <tr>
                <td colspan="9" class="sin-datos" style="color: red;">
                    Error al conectar con Spring Boot. Verifique que el servidor esté activo.
                </td>
            </tr>`;
    }
}

function renderizarTabla(insumos) {
    const tbody = document.getElementById("tablaInsumosBody");
    tbody.innerHTML = "";

    if (!insumos || insumos.length === 0) {
        tbody.innerHTML = `<tr><td colspan="9" class="sin-datos">No se encontraron insumos.</td></tr>`;
        return;
    }

    insumos.forEach(insumo => {
        const idVal = insumo.idInsumo || insumo.id || '';
        const estaDisponible = insumo.stockActual > 0;
        const estadoClase = estaDisponible ? 'activo' : 'inactivo';
        const estadoTexto = estaDisponible ? 'Disponible' : 'No Disponible';

        const tr = document.createElement("tr");
        tr.innerHTML = `
            <td>${insumo.nombreInsumo || 'Sin Nombre'}</td>
            <td>${insumo.descripcionInsumo || '-'}</td>
            <td>${insumo.categoriaInsumo || '-'}</td>
            <td>${insumo.fechaVencimiento || '-'}</td>
            <td>${insumo.stockActual ?? 0}</td>
            <td>${insumo.stockMinPosible ?? 0}</td>
            <td>${insumo.horaActualizacion || '-'}</td>
            <td><span class="estado ${estadoClase}">${estadoTexto}</span></td>
            <td>
                <div class="acciones">
                    <a class="btn-accion editar" href="editarI.html?id=${idVal}" title="Actualizar">
                        <i class='bx bx-edit'></i>
                    </a>
                    <button class="btn-accion eliminar" onclick="eliminarInsumo(${idVal})" title="Eliminar">
                        <i class='bx bx-trash'></i>
                    </button>
                </div>
            </td>
        `;
        tbody.appendChild(tr);
    });
}

function renderizarPaginacion(data) {
    const contenedorPaginacion = document.getElementById("pagination-container");
    if (!contenedorPaginacion) return;

    contenedorPaginacion.innerHTML = "";

    // Botón Anterior
    const btnAnt = document.createElement("button");
    btnAnt.className = "btn-pag";
    btnAnt.textContent = "Anterior";
    btnAnt.disabled = data.first;
    btnAnt.onclick = () => cargarInsumos(data.number - 1);
    contenedorPaginacion.appendChild(btnAnt);

    // Botones numéricos estilo Servicios
    const maxBotones = 5;
    let inicio = Math.max(0, data.number - Math.floor(maxBotones / 2));
    let fin = Math.min(data.totalPages, inicio + maxBotones);

    if (fin - inicio < maxBotones) {
        inicio = Math.max(0, fin - maxBotones);
    }

    for (let i = inicio; i < fin; i++) {
        const btnPage = document.createElement("button");
        btnPage.className = `btn-pag ${i === data.number ? 'actual' : ''}`;
        btnPage.textContent = i + 1;
        btnPage.onclick = () => cargarInsumos(i);
        contenedorPaginacion.appendChild(btnPage);
    }

    // Botón Siguiente
    const btnSig = document.createElement("button");
    btnSig.className = "btn-pag";
    btnSig.textContent = "Siguiente";
    btnSig.disabled = data.last;
    btnSig.onclick = () => cargarInsumos(data.number + 1);
    contenedorPaginacion.appendChild(btnSig);
}

function filtrarCategoria(categoria, elementoBtn) {
    categoriaActual = categoria;
    paginaActual = 0;

    if (elementoBtn) {
        const grupo = elementoBtn.closest('.grupo-filtro');
        if (grupo) {
            grupo.querySelectorAll('.btn-filtro').forEach(b => b.classList.remove('active'));
            elementoBtn.classList.add('active');
        }
    }

    cargarInsumos(paginaActual);
}

function filtrarEstado(estado, elementoBtn) {
    estadoActual = estado;
    paginaActual = 0;

    if (elementoBtn) {
        const grupo = elementoBtn.closest('.grupo-filtro');
        if (grupo) {
            grupo.querySelectorAll('.btn-filtro').forEach(b => b.classList.remove('active'));
            elementoBtn.classList.add('active');
        }
    }

    cargarInsumos(paginaActual);
}

async function eliminarInsumo(id) {
    if (!confirm("¿Está seguro de que desea eliminar este insumo?")) return;

    try {
        const response = await fetch(`${API_URL}/${id}`, { method: "DELETE" });
        if (response.ok) {
            alert("Insumo eliminado correctamente.");
            cargarInsumos(paginaActual);
        } else {
            alert("No se pudo eliminar el insumo.");
        }
    } catch (error) {
        console.error("Error al eliminar:", error);
    }
}

function inicializarSidebar() {
    const menusItemsDropDown = document.querySelectorAll('.menu-item-dropdown');
    const sidebar = document.getElementById('sidebar');
    const menuBtn = document.getElementById('menu-btn');
    const sidebarBtn = document.getElementById('sidebar-btn');

    sidebarBtn?.addEventListener('click', () => {
        document.body.classList.toggle('sidebar-hidden');
    });

    menuBtn?.addEventListener('click', () => {
        sidebar?.classList.toggle('minimize');
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
}