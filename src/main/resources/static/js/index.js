function redireccionar(button) {
  const url = button.getAttribute("data-url-redireccion");
  console.log("Redirigiendo a:", url);
  window.location.href = url;
}

// FUNCIONES PARA EL RELOJ
function updateClock() {
  const clock = document.getElementById("digital-clock");
  const dateElement = document.getElementById("digital-date");

  const now = new Date();

  // Obtener la hora
  let hours = now.getHours();
  const minutes = String(now.getMinutes()).padStart(2, "0");
  const ampm = hours >= 12 ? "PM" : "AM";
  hours = hours % 12 || 12; // Convertir a formato 12 horas

  // Obtener la fecha
  const days = [
    "DOMINGO",
    "LUNES",
    "MARTES",
    "MIÉRCOLES",
    "JUEVES",
    "VIERNES",
    "SÁBADO",
  ];
  const dayName = days[now.getDay()];
  const dayNumber = now.getDate();

  // Mostrar la fecha y hora
  dateElement.textContent = `${dayName} ${dayNumber}`;
  clock.textContent = `${hours}:${minutes} ${ampm}`;
}

// Actualizar el reloj cada segundo
setInterval(updateClock, 1000);
updateClock(); // Llamar inmediatamente para evitar retrasos

const btn_editar_1 = document.getElementById("btn-editar-1");
const btn_editar_2 = document.getElementById("btn-editar-2");

// Leer el parámetro fecha de la URL
const params_2 = new URLSearchParams(window.location.search);
const fechaURL_2 = params_2.get("fechaFiltro");

// Si hay una fecha en la URL, calcular cuántos días se ha desplazado desde hoy
if (fechaURL_2) {
  btn_editar_1.setAttribute("disabled", "true");
  btn_editar_2.setAttribute("disabled", "true");
} else {
  btn_editar_1.removeAttribute("disabled");
  btn_editar_2.removeAttribute("disabled");
}