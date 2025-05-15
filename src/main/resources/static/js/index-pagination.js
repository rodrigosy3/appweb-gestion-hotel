const ulFechas = document.getElementById("barra-fechas");
const mesAnioTitulo = document.getElementById("mes-anio");
const inputFiltroFecha = document.getElementById("filtroFecha");

let diasDesplazados = 0;

// Leer el parámetro fecha de la URL
const params = new URLSearchParams(window.location.search);
const fechaURL = params.get("fechaFiltro");

// Si hay una fecha en la URL, calcular cuántos días se ha desplazado desde hoy
if (fechaURL) {
    const hoy = new Date();
    hoy.setHours(0,0,0,0);
    const fechaFiltro = new Date(fechaURL + "T00:00:00");
    const diferencia = Math.floor((fechaFiltro - hoy) / (1000 * 60 * 60 * 24));
    diasDesplazados = diferencia;
}

// Función principal que renderiza los 5 días (2 antes, hoy, 2 después)
function renderizarFechas() {
    const hoy = new Date();
    hoy.setHours(0, 0, 0, 0); // Quitar hora

    const inicio = new Date(hoy);
    inicio.setDate(hoy.getDate() + diasDesplazados);

    // Eliminar fechas anteriores (dejar solo flechas)
    ulFechas.querySelectorAll(".dia-item").forEach(e => e.remove());

    for (let i = -3; i <= 3; i++) {
        const fecha = new Date(inicio);
        fecha.setDate(inicio.getDate() + i);

        const fechaISO = formatoFechaLocalISO(fecha);
        const dia = fecha.getDate();

        const esHoy = compararFechas(fecha, new Date());
        const esSeleccionada = i === 0;

        const li = document.createElement("li");
        li.className = `page-item dia-item ${esSeleccionada ? 'active' : ''}`;

        const a = document.createElement("a");
        a.className = "page-link";
        a.textContent = dia;
        a.title = fechaISO;

        a.onclick = () => seleccionarFecha(fecha);

        li.appendChild(a);
        ulFechas.insertBefore(li, document.getElementById("next"));
    }

    // Actualizar título con mes y año del día central
    const central = new Date(inicio);
    const mesActual = central.toLocaleString("es-PE", { month: "long", year: "numeric" });
    mesAnioTitulo.textContent = mesActual.charAt(0).toUpperCase() + mesActual.slice(1);
}

// Cambia el centro hacia un día anterior (-1) o siguiente (+1)
function cambiarRango(direccion) {
    diasDesplazados += direccion;
    const nuevaFecha = new Date();
    nuevaFecha.setDate(nuevaFecha.getDate() + diasDesplazados);

    actualizarURLyInput(nuevaFecha);
    renderizarFechas();
}

// Acción del botón "Hoy"
function irAHoy() {
    diasDesplazados = 0;

    inputFiltroFecha.value = "";
    limpiarURL();

    renderizarFechas();
}

// Selección de una fecha desde los botones de día
function seleccionarFecha(fecha) {
    const hoy = new Date();
    hoy.setHours(0, 0, 0, 0);
    fecha.setHours(0, 0, 0, 0);

    diasDesplazados = Math.floor((fecha - hoy) / (1000 * 60 * 60 * 24));

    actualizarURLyInput(fecha);
    renderizarFechas();
}

// Convierte una fecha a formato yyyy-MM-dd
function formatoFechaLocalISO(fecha) {
    const anio = fecha.getFullYear();
    const mes = String(fecha.getMonth() + 1).padStart(2, '0');
    const dia = String(fecha.getDate()).padStart(2, '0');
    return `${anio}-${mes}-${dia}`;
}

// Compara si dos fechas (sin hora) son iguales
function compararFechas(f1, f2) {
    return f1.toDateString() === f2.toDateString();
}

// Actualiza la URL con el parámetro fechaFiltro si no es hoy
function actualizarURLyInput(fecha) {
    const hoy = new Date();
    hoy.setHours(0, 0, 0, 0);
    fecha.setHours(0, 0, 0, 0);

    const esHoy = compararFechas(hoy, fecha);
    const fechaISO = formatoFechaLocalISO(fecha);

    if (esHoy) {
        window.location.href = `/`;
    } else {
        // const nuevaURL = `?fechaFiltro=${fechaISO}`;
        // window.history.replaceState({}, "", nuevaURL);
        // inputFiltroFecha.value = fechaISO;

        window.location.href = `?fechaFiltro=${fechaISO}`;
    }
}

// Inicializar barra al cargar
renderizarFechas();

// document.getElementById("filtroFecha").addEventListener("change", function() {
//     const fecha = this.value;
//     fetch(`/ventas?fecha=${fecha}`)
//         .then(response => response.text())
//         .then(html => {
//             document.getElementById("tablaVentas").innerHTML = html;
//         });
// });