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