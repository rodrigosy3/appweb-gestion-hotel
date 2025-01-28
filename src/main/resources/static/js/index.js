function redireccionar(button) {
  const url = button.getAttribute("data-url-redireccion");
  console.log("Redirigiendo a:", url);
  window.location.href = url;
}

// FUNCIONES PARA EL RELOJ
function updateClock() {
  const clock = document.getElementById("digital-clock");
  const now = new Date();
  let hours = now.getHours();
  const minutes = String(now.getMinutes()).padStart(2, "0");
  const ampm = hours >= 12 ? "PM" : "AM";
  hours = hours % 12 || 12; // Convertir a formato 12 horas
  clock.textContent = `${hours}:${minutes} ${ampm}`;
}

// Actualizar el reloj cada segundo
setInterval(updateClock, 1000);
updateClock(); // Llamar inmediatamente para evitar retrasos

//CONSUILTAS DNI
document.getElementById("btnBuscarDNI").addEventListener("click", function () {
  const dni = document.getElementById("inputDNI").value;

  if (dni.length !== 8) {
    alert("El DNI debe tener 8 dígitos.");
    return;
  }

  const apiUrl = `http://localhost:3000/api/dni?numero=${dni}`; // Cambia el puerto según tu configuración de Spring Boot

  fetch(apiUrl)
    .then((response) => {
      console.log("Estado de la respuesta:", response.status);
      if (!response.ok) {
        throw new Error("Error al consultar el backend.");
      }
      return response.json();
    })
    .then((data) => {
      console.log("Datos recibidos del backend:", data);
      if (data.numeroDocumento) {
        document.getElementById("inputNombres").value = data.nombres || "";
        document.getElementById("inputApellidos").value = `${
          data.apellidoPaterno || ""
        } ${data.apellidoMaterno || ""}`.trim();
      } else {
        alert("No se encontró información para este DNI.");
      }
    })
    .catch((error) => {
      console.error("Error al realizar la consulta:", error);
      alert("No se pudo obtener información del DNI.");
    });
});
