document.addEventListener("DOMContentLoaded", function () {
    const inputDNI = document.getElementById("inputDNI");
    const inputNombres = document.getElementById("inputNombres");
    const inputApellidos = document.getElementById("inputApellidos");
    const tablaClientesBody = document.getElementById("tablaClientesBody");
    const idVentaInput = document.getElementById("idVenta");

    let clientesTemporales = []; // Clientes agregados y a agregar en esta sesión

    // Cargar los clientes ya registrados en la lista temporal
    if (idVentaInput && idVentaInput.value.trim() !== "") {
        document.querySelectorAll("#tablaClientesBody tr").forEach(row => {
            let dni = row.cells[0].innerText;
            let nombres = row.cells[1].innerText;
            let apellidos = row.cells[2].innerText;
            clientesTemporales.push({ dni, nombres, apellidos, eliminado: false });
        });
    }

    if (idVentaInput) { actualizarEstadoBoton() };
    // Función para agregar un cliente temporalmente
    function agregarCliente() {
        const dni = inputDNI.value.trim();
        const nombres = inputNombres.value.trim().toUpperCase();
        const apellidos = inputApellidos.value.trim().toUpperCase();

        if (!dni || !nombres || !apellidos) {
            alert("Por favor, completa todos los campos del cliente.");
            return;
        }

        if (dni.length < 8) {
            alert("El DNI debe contener 8 números.");
            return;
        }

        let clienteExistente = clientesTemporales.find(cliente => cliente.dni === dni);

        if (clienteExistente) {
            if (clienteExistente.eliminado) {
                // Si estaba eliminado, lo reactivamos
                clienteExistente.eliminado = false;
            } else {
                alert("Este cliente ya está agregado.");
                return;
            }
        } else {
            // Si el cliente no existe, lo agregamos como nuevo
            clientesTemporales.push({ dni, nombres, apellidos, eliminado: false });
        }

        const fila = document.createElement("tr");
        fila.innerHTML = `
                    <td class="border align-middle">${dni}</td>
                    <td class="align-middle">${nombres}</td>
                    <td class="align-middle">${apellidos}</td>
                    <td class="text-center align-middle border">
                        <button type="button" class="btn btn-danger btn-sm" onclick="eliminarCliente(this, '${dni}')">X</button>
                    </td>
                `;
        tablaClientesBody.appendChild(fila);

        inputDNI.value = "";
        inputNombres.value = "";
        inputApellidos.value = "";

        if (clientesTemporales.length == 1) { actualizarEstadoBoton(); }
    }

    // Función para eliminar un cliente temporalmente
    window.eliminarCliente = function (button, dni) {
        dni = String(dni);

        clientesTemporales.forEach(cliente => {
            if (cliente.dni === dni) {
                cliente.eliminado = true;
            }
        });

        button.closest("tr").remove();
    };

    function bloquearBtnAgregarCliente(idBtn) {
        const btnAgregar = document.getElementById(idBtn);
        btnAgregar.classList.remove("btn-success"); // Quita el estilo verde
        btnAgregar.classList.add("btn-danger"); // Aplica el estilo rojo
        btnAgregar.setAttribute("disabled", "true"); // Deshabilita el botón
    }

    function desbloquearBtnAgregarCliente(idBtn) {
        const btnAgregar = document.getElementById(idBtn);
        btnAgregar.classList.remove("btn-danger"); // Quita el estilo rojo
        btnAgregar.classList.add("btn-success"); // Aplica el estilo verde
        btnAgregar.removeAttribute("disabled"); // Habilita el botón
    }

    // Función para buscar cliente en la BD antes de la API externa
    function buscarClienteEnBD() {
        const dni = inputDNI.value.trim();

        if (dni < 8) {
            alert("El DNI debe contener 8 números.");
            return;
        }

        fetch(`/buscar-cliente?dni=${dni}`, {
            method: "GET"
        }).then(response => response.json()).then(data => {
            if (data.encontrado) {
                inputNombres.value = data.nombres;
                inputApellidos.value = data.apellidos;

                if (data.estado_vetado) {
                    // Mostrar modal con la razón del veto
                    document.getElementById('razonVeto').innerText = data.razon_vetado;
                    let modalVetado = new bootstrap.Modal(document.getElementById('modalVetado'));
                    modalVetado.show();

                    // Bloquear el registro del cliente
                    bloquearBtnAgregarCliente("btnAgregarCliente");

                } else {
                    // Manejar el caso en que el cliente no exista
                    desbloquearBtnAgregarCliente("btnAgregarCliente");
                }
            } else {
                console.log("Cliente no encontrado en la base de datos. Consultando API externa...");
                buscarClienteEnAPI(dni);
            }
        }).catch(error => console.error("Error buscando cliente:", error));
    }

    function buscarClienteEnAPI(dni) {
        if (dni.length !== 8) {
            alert("El DNI debe tener 8 dígitos.");
            return;
        }

        const apiUrl = `http://localhost:3000/api/dni?numero=${dni}`; // Cambia el puerto según la configuración de Spring Boot

        fetch(apiUrl).then(response => {
            console.log("Estado de la respuesta:", response.status);
            if (!response.ok) {
                throw new Error("Error al consultar el backend.");
            }
            return response.json();
        }).then(data => {
            if (data.numeroDocumento) {
                document.getElementById("inputNombres").value = data.nombres || "";
                document.getElementById("inputApellidos").value = `${data.apellidoPaterno || ""
                    } ${data.apellidoMaterno || ""}`.trim();
            } else {
                alert("No se encontró información para este DNI. Agregar manualmente");
            }
        }).catch(error => {
            console.error("Error al realizar la consulta:", error);
        });
    }

    function actualizarEstadoBoton() {
        if (clientesTemporales.length > 0) {
            document.getElementById("btnGuardar").disabled = false;
        } else {
            document.getElementById("btnGuardar").disabled = true;
        }
    }

    function guardarVenta(event) {
        event.preventDefault(); // Evitar envío normal del formulario

        // Obtener el formulario
        let form = document.getElementById("formVentaHabitacion");

        // Objeto FormData con los datos del formulario
        let formData = new FormData(form);

        // Convertir clientesTemporales a JSON y agregarlo al formData
        formData.append("clientesTemporales", JSON.stringify(clientesTemporales));

        // Enviar los datos al backend
        fetch(form.action, {
            method: "POST",
            body: formData
        }).then(response => {
            if (!response.ok) {
                console.log("Error al guardar la venta");
            }

            window.location.href = "/"; // Redirigir a la página principal después de guardar
        }).catch(error => console.error("Error:", error));
    };

    function retirarCliente() {
        const idVenta = idVentaInput.value;  // Obtiene el ID de la venta

        if (!idVenta) {
            alert("No se puede actualizar el estado sin una venta registrada.");
            return;
        }

        fetch(`/actualizar-estado/${idVenta}`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            }
        }).then(response => {
            if (!response.ok) {
                throw new Error("Error al actualizar el estado de la venta");
            }
            return response.json();
        }).then(data => {
            console.log("Estado actualizado correctamente");
            //location.reload(); // Recargar la página para ver el cambio reflejado
            window.location.href = "/";
        }).catch(error => {
            console.error("Error:", error);
            alert("Hubo un problema al actualizar el estado.");
        });
    };

    //######################################################################################################################################################################

    // FECHAS EN SERVICIO DE ALOJAMIENTO
    const resumenFechaEntrada = document.getElementById("fechaEntradaResumen");
    const resumenFechaEntradaValor = document.getElementById("fechaEntradaResumenValor");
    const resumenFechaSalida = document.getElementById("fechaSalidaResumen");
    const resumenFechaSalidaValor = document.getElementById("fechaSalidaResumenValor");
    const resumenDiasAlojamiento = document.getElementById("diasAlojamientoResumen");

    const resumenSelectServicio = document.getElementById("selectTipoServicioResumen");
    const resumenSelectEstado = document.getElementById("selectEstadoResumen");

    const resumenDescuento = document.getElementById("descuentoResumen");
    const resumenMontoAdelanto = document.getElementById("montoAdelantoResumen");
    const resumenMontoTotal = document.getElementById("montoTotalResumen");

    const precioInput = document.getElementById("habitacionPrecio");
    const tablaFechasBody = document.getElementById("tableBodyFechasAlojamiento");

    const fechaActual = new Date();
    let fechaEntradaValor;

    // **(1) La fecha de entrada vacía tomará la del resumen o la actual**
    if (idVentaInput.value !== null && idVentaInput.value.trim() !== "") {
        fechaEntradaValor = new Date(resumenFechaEntradaValor.value);

        resumenFechaEntrada.value = fechaParseString(fechaEntradaValor);
        resumenFechaSalida.value = fechaParseString(new Date(resumenFechaSalidaValor.value));
    } else {
        fechaEntradaValor = new Date(fechaActual);
        resumenFechaEntrada.value = fechaParseString(fechaEntradaValor);
    }

    manejarBloqueoServicio();

    // **(5) Bloquear servicio si hay más de un día**
    function manejarBloqueoServicio() {
        if (parseInt(resumenDiasAlojamiento.value) < 2) {
            resumenSelectServicio.value = "COMPLETO";
            resumenSelectServicio.setAttribute("disabled", true);
        } else {
            resumenSelectServicio.removeAttribute("disabled");
        }

        actualizarFilas();
    }

    // **(3) Calcular importe según tipo de servicio**
    function calcularImporte(servicio) {
        let precioBase = parseFloat(precioInput.getAttribute("data-precio")) || 0;

        return servicio == "MEDIO" ? (precioBase / 2).toFixed(2) : precioBase.toFixed(2);
    }

    // **(6) Calcular monto total**
    function calcularMontoTotal() {
        let tipo_servicio = resumenSelectServicio.options[resumenSelectServicio.selectedIndex];
        let dias = parseInt(resumenDiasAlojamiento.value) || 1;
        let descuento = parseFloat(resumenDescuento.value) || 0;
        let adelanto = parseFloat(resumenMontoAdelanto.value) || 0;
        let precioBase = parseFloat(precioInput.getAttribute("data-precio")) || 0;

        return tipo_servicio.getAttribute("data-tipo").includes("MEDIO") ? ((precioBase * (dias - 1)) + (precioBase / 2) - descuento - adelanto).toFixed(2) : ((dias * precioBase) - descuento - adelanto).toFixed(2);
    }

    // **(4) Calcular fecha de salida**
    function calcularFechaSalida(fecha, tipoServicio) {
        let fechaSalida = new Date(fecha);

        if (tipoServicio.includes("COMPLETO")) {
            if (fechaEntradaValor.getHours() >= 6) {
                fechaSalida.setDate(fechaSalida.getDate() + 1); // Día siguiente
            }
            fechaSalida.setHours(12, 0, 0); // Medio día del mismo día
        } else {
            fechaSalida.setHours(18, 0, 0); // 6 PM del mismo día
        }

        return fechaSalida;
    }

    // **(2) Manejar el cambio de días de alojamiento**
    function actualizarFilas() {
        const dias = parseInt(resumenDiasAlojamiento.value) || 1;
        tablaFechasBody.innerHTML = ""; // Limpiar la tabla antes de reconstruirla
        let tipo_servicio = resumenSelectServicio.options[resumenSelectServicio.selectedIndex];

        let fechaBase = new Date(fechaEntradaValor);

        for (let i = 1; i <= dias; i++) {
            let nuevaFila = document.createElement("tr");
            let servicio = (i === dias && tipo_servicio.getAttribute("data-tipo") === "MEDIO") ? "MEDIO" : "COMPLETO";

            let fechaSalida = calcularFechaSalida(fechaBase, servicio);

            nuevaFila.innerHTML = `
                            <td class="align-middle text-center">
                                <span class="bg-success-subtle rounded-4 p-2">${fechaParseStringDiaHora(fechaBase)}</span>
                            </td>
                            <td class="align-middle text-center">
                                <span class="bg-secondary-subtle rounded-4 p-2">
                                    ${servicio === "COMPLETO" ? "COMPLETO" : "MEDIO DÍA"}
                                </span>
                            </td>
                            <td class="align-middle text-center"><span class="bg-danger-subtle rounded-4 p-2">${fechaParseStringDiaHora(fechaSalida)}</span></td>
                            <td class="align-middle text-center"><span class="mx-3">${'S/. ' + calcularImporte(servicio)}</span></td>
                        `;

            tablaFechasBody.appendChild(nuevaFila);

            fechaBase = new Date(fechaSalida);
        }

        actualizarResumen();
    }

    function actualizarResumen() {
        let tipo_servicio = resumenSelectServicio.options[resumenSelectServicio.selectedIndex];
        let dias = parseInt(resumenDiasAlojamiento.value) || 1;

        let fechaBase = new Date(fechaEntradaValor);
        let fechaSalida = "";

        resumenFechaEntradaValor.value = fechaJsToJavaParseString(fechaEntradaValor);

        if (dias > 1) {
            if (tipo_servicio.getAttribute("data-tipo") == "COMPLETO") {
                fechaBase.setDate(fechaBase.getDate() + dias);
                fechaBase.setHours(12, 0, 0); // Medio día
            } else {
                fechaBase.setDate(fechaBase.getDate() + dias);
                fechaBase.setHours(18, 0, 0); // 6 PM
            }

            resumenFechaSalidaValor.value = fechaJsToJavaParseString(fechaBase);
            fechaSalida = fechaParseString(fechaBase);
        } else {
            resumenFechaSalidaValor.value = fechaJsToJavaParseString(calcularFechaSalida(fechaBase, "COMPLETO"));
            fechaSalida = fechaParseString(calcularFechaSalida(fechaBase, "COMPLETO"));
        }

        resumenMontoTotal.value = calcularMontoTotal();
        resumenFechaSalida.value = fechaSalida;

        if (parseFloat(resumenMontoTotal.value) === 0.0) {
            resumenSelectEstado.value = "PAGADO";
            resumenSelectEstado.setAttribute("disabled", true);
        } else {
            resumenSelectEstado.value = "POR COBRAR";
            resumenSelectEstado.removeAttribute("disabled");
        }
    }

    function fechaJsToJavaParseString(fecha) {
        const anio = fecha.getFullYear();
        const mes = String(fecha.getMonth() + 1).padStart(2, '0');
        const dia = String(fecha.getDate()).padStart(2, '0');
        const horas = String(fecha.getHours()).padStart(2, '0');
        const minutos = String(fecha.getMinutes()).padStart(2, '0');
        const segundos = String(fecha.getSeconds()).padStart(2, '0');

        return `${anio}-${mes}-${dia}T${horas}:${minutos}:${segundos}`;
    }

    // **Eventos**
    if (resumenFechaEntrada) {
        resumenFechaEntrada.addEventListener("change", actualizarFilas);
    }
    if (resumenDiasAlojamiento) {
        resumenDiasAlojamiento.addEventListener("input", manejarBloqueoServicio);
    }
    if (resumenSelectServicio) {
        resumenSelectServicio.addEventListener("change", actualizarFilas);
    }
    if (resumenDescuento) {
        resumenDescuento.addEventListener("input", actualizarResumen);
    }
    if (resumenMontoAdelanto) {
        resumenMontoAdelanto.addEventListener("input", actualizarResumen);
    }

    // Formatear fecha a texto 'YYYY-MM-DD hh:mm A'
    function fechaParseString(fecha) {
        const opcionesFecha = { year: 'numeric', month: '2-digit', day: '2-digit' };
        const opcionesHora = { hour: '2-digit', minute: '2-digit', hour12: true };
        const fechaFormateada = fecha.toLocaleDateString('es-ES', opcionesFecha).split('/').join('-');
        const horaFormateada = fecha.toLocaleTimeString('es-ES', opcionesHora).replace('.', '.');

        return `${fechaFormateada}   [${horaFormateada}]`;
    }

    // Formatear fecha a texto '|Día| Hora'
    function fechaParseStringDiaHora(fecha) {
        const opcionesFecha = { day: '2-digit' };
        const opcionesHora = { hour: '2-digit', minute: '2-digit', hour12: true };
        const fechaFormateada = fecha.toLocaleDateString('es-ES', opcionesFecha);
        const horaFormateada = fecha.toLocaleTimeString('es-ES', opcionesHora).replace('.', '.');

        return `| ${fechaFormateada} | ${horaFormateada}`;
    }

    function cambiarTabEstado(estado) {
        if (estado === "RESERVADA") {
            let estadoTab = document.querySelector('[data-bs-target="#reserva-tab"]');
            if (estadoTab) {
                new bootstrap.Tab(estadoTab).show();
            }
        } else if (estado === "OCUPADO") {
            let estadoTab = document.querySelector('[data-bs-target="#venta-tab"]');
            if (estadoTab) {
                new bootstrap.Tab(estadoTab).show();
            }
        } else {
            let estadoTab = document.querySelector('[data-bs-target="#estado-tab"]');
            if (estadoTab) {
                new bootstrap.Tab(estadoTab).show();
            }
        }
    }

    document.addEventListener("click", function (event) {
        if (event.target.id === "btnRetirarCliente") {
            retirarCliente();
        }
        if (event.target.id === "btnAgregarCliente") {
            agregarCliente();
        }
        if (event.target.id === "btnBuscarDNI") {
            buscarClienteEnBD();
        }
        if (event.target.id === "btnGuardar") {
            guardarVenta(event);
        }

        // Cambio de targets
        if (event.target.id === "btnVerDetallesMantenimiento") {
            cambiarTabEstado("MANTENIMIENTO");
        }
        if (event.target.id === "btnVerDetallesNoDisponible") {
            cambiarTabEstado("NO DISPONIBLE");
        }
        if (event.target.id === "btnVerDetallesReserva") {
            cambiarTabEstado("RESERVADA");
        }
        if (event.target.id === "btnVerDetallesOcupado") {
            cambiarTabEstado("OCUPADO");
        }

        if (event.target.id === "reservabtnRetirarReserva") {
            reservaEliminarReserva();
        }
        if (event.target.id === "reservabtnHabilitarReserva") {
            reservaCambiarEstadoReserva();
        }
        if (event.target.id === "reservabtnAgregarCliente") {
            reservaagregarCliente();
        }
        if (event.target.id === "reservabtnBuscarDNI") {
            reservabuscarClienteEnBD();
        }
        if (event.target.id === "reservabtnGuardar") {
            reservaguardarReserva(event);
        }
    });

















    const reservainputDNI = document.getElementById("reservainputDNI");
    const reservainputNombres = document.getElementById("reservainputNombres");
    const reservainputApellidos = document.getElementById("reservainputApellidos");
    const reservatablaClientesBody = document.getElementById("reservatablaClientesBody");
    const reservaidVentaInput = document.getElementById("reservaidVenta");

    let reservaclientesTemporales = []; // Clientes agregados y a agregar en esta sesión

    // Cargar los clientes ya registrados en la lista temporal
    if (reservaidVentaInput && reservaidVentaInput.value.trim() !== "") {
        document.querySelectorAll("#reservatablaClientesBody tr").forEach(row => {
            let dni = row.cells[0].innerText;
            let nombres = row.cells[1].innerText;
            let apellidos = row.cells[2].innerText;
            reservaclientesTemporales.push({ dni, nombres, apellidos, eliminado: false });
        });
    }
    if (reservaidVentaInput) { reservaactualizarEstadoBoton(); }

    // Función para agregar un cliente temporalmente
    function reservaagregarCliente() {
        const dni = reservainputDNI.value.trim();
        const nombres = reservainputNombres.value.trim().toUpperCase();
        const apellidos = reservainputApellidos.value.trim().toUpperCase();

        if (!dni || !nombres || !apellidos) {
            alert("Por favor, completa todos los campos del cliente.");
            return;
        }

        let ClienteExistente = reservaclientesTemporales.find(cliente => cliente.dni === dni);

        if (ClienteExistente) {
            if (ClienteExistente.eliminado) {
                // Si estaba eliminado, lo reactivamos
                ClienteExistente.eliminado = false;
            } else {
                alert("Este cliente ya está agregado.");
                return;
            }
        } else {
            // Si el cliente no existe, lo agregamos como nuevo
            reservaclientesTemporales.push({ dni, nombres, apellidos, eliminado: false });
        }

        const fila = document.createElement("tr");
        fila.innerHTML = `
                    <td class="border align-middle">${dni}</td>
                    <td class="align-middle">${nombres}</td>
                    <td class="align-middle">${apellidos}</td>
                    <td class="text-center align-middle border">
                        <button type="button" class="btn btn-danger btn-sm" onclick="reservaeliminarCliente(this, '${dni}')">X</button>
                    </td>
                `;
        reservatablaClientesBody.appendChild(fila);

        reservainputDNI.value = "";
        reservainputNombres.value = "";
        reservainputApellidos.value = "";

        if (reservaclientesTemporales.length == 1) { reservaactualizarEstadoBoton(); }
    }

    // Función para eliminar un cliente temporalmente
    window.reservaeliminarCliente = function (button, dni) {
        dni = String(dni);  // Convertir siempre a string

        reservaclientesTemporales.forEach(cliente => {
            if (cliente.dni === dni) {
                cliente.eliminado = true;
            }
        });

        button.closest("tr").remove();
    };

    // Función para buscar cliente en la BD antes de la API externa
    function reservabuscarClienteEnBD() {
        const dni = reservainputDNI.value.trim();

        if (dni < 8) {
            alert("El DNI debe contener 8 números.");
            return;
        }

        fetch(`/buscar-cliente?dni=${dni}`, {
            method: "GET"
        }).then(response => response.json()).then(data => {
            if (data.encontrado) {
                reservainputNombres.value = data.nombres;
                reservainputApellidos.value = data.apellidos;

                if (data.estado_vetado) {
                    // Mostrar modal con la razón del veto
                    document.getElementById('razonVeto').innerText = data.razon_vetado;
                    let modalVetado = new bootstrap.Modal(document.getElementById('modalVetado'));
                    modalVetado.show();

                    // Bloquear el registro del cliente
                    bloquearBtnAgregarCliente("reservabtnAgregarCliente");

                } else {
                    // Manejar el caso en que el cliente no exista
                    desbloquearBtnAgregarCliente("reservabtnAgregarCliente");
                }
            } else {
                console.log("Cliente no encontrado en la base de datos. Consultando API externa...");
                reservabuscarClienteEnAPI(dni);
            }
        }).catch(error => console.error("Error buscando cliente:", error));
    }

    function reservabuscarClienteEnAPI(dni) {
        if (dni.length !== 8) {
            alert("El DNI debe tener 8 dígitos.");
            return;
        }

        const apiUrl = `http://localhost:3000/api/dni?numero=${dni}`; // Cambia el puerto según la configuración de Spring Boot

        fetch(apiUrl).then(response => {
            console.log("Estado de la respuesta:", response.status);
            if (!response.ok) {
                throw new Error("Error al consultar el backend.");
            }
            return response.json();
        }).then(data => {
            if (data.numeroDocumento) {
                document.getElementById("reservainputNombres").value = data.nombres || "";
                document.getElementById("reservainputApellidos").value = `${data.apellidoPaterno || ""
                    } ${data.apellidoMaterno || ""}`.trim();
            } else {
                alert("No se encontró información para este DNI. Agregar manualmente");
            }
        }).catch(error => {
            console.error("Error al realizar la consulta:", error);
            alert("No se encontró información para este DNI. Agregar manualmente");
        });
    }

    function reservaactualizarEstadoBoton() {
        if (reservaclientesTemporales.length > 0) {
            document.getElementById("reservabtnGuardar").disabled = false;
        } else {
            document.getElementById("reservabtnGuardar").disabled = true;
        }
    }

    function reservaguardarReserva(event) {
        event.preventDefault(); // Evita el envío normal del formulario

        // Obtener el formulario
        let form = document.getElementById("reservaformVentaHabitacion");

        // Crear objeto FormData con los datos del formulario
        let formData = new FormData(form);

        // Convertir reservaclientesTemporales a JSON y agregarlo al formData
        formData.append("reservaclientesTemporales", JSON.stringify(reservaclientesTemporales));

        // Enviar los datos al backend
        fetch(form.action, {
            method: "POST",
            body: formData
        }).then(response => {
            if (response.ok) {
                window.location.href = "/"; // Redirigir a la página principal después de guardar
            } else {
                alert("Error al guardar la venta");
            }
        }).catch(error => console.error("Error:", error));
    };

    function reservaEliminarReserva() {
        const idVentaReservada = reservaidVentaInput.value;
        console.log(idVentaReservada);
        if (!idVentaReservada) {
            console.log("No se puede cancelar una reserva no registrada.");
            return;
        }

        fetch(`/cancelar-reserva/${idVentaReservada}`, {
            method: "POST"
        }).then(response => {
            if (!response.ok) {
                throw new Error("Error al cancelar la reserva");
            }
            return response.text();
        }).then(data => {
            alert("Reserva cancelada correctamente");
            //location.reload(); // Recargar la página para ver el cambio reflejado
            window.location.href = "/";
        }).catch(error => {
            console.error("Error:", error);
            console.error("Hubo un problema al cancelar la reserva.");
        });
    }

    function reservaCambiarEstadoReserva() {
        const idVenta = reservaidVentaInput.value; // Obtiene el ID de la venta

        if (!idVenta) {
            console.log("No se puede finalizar reserva no registrada.");
            return;
        }

        fetch(`/habilitar-reserva/${idVenta}`, {
            method: "POST"
        }).then(response => {
            if (!response.ok) {
                throw new Error("Error al actualizar el estado de la venta");
            }
            return response.text(); // El controlador devuelve una redirección, no JSON
        }).then(() => {
            console.log("Reserva habilitada correctamente");
            window.location.href = "/"; // Redirigir a la página de inicio
        }).catch(error => {
            console.error("Error:", error);
            alert("Hubo un problema al habilitar la reserva.");
        });
    };


    // FECHAS
    const reservafechaSalidaResumen = document.getElementById("reservafechaSalidaResumen");
    const reservafechaEntradaResumen = document.getElementById("reservafechaEntradaResumen");
    const reservadiasAlojamientoResumen = document.getElementById("reservatiempoEstadiaResumen");

    const reservaselectServicio = document.getElementById("reservaselectTipoServicioResumen");

    const reservadescuentoResumen = document.getElementById("reservadescuentoResumen");
    const reservamontoAdelantoResumen = document.getElementById("reservamontoAdelantoResumen");
    const reservamontoTotalResumen = document.getElementById("reservamontoTotalResumen");
    const reservatablaFechasBody = document.getElementById("reservatableBodyFechasAlojamiento");
    const reservaprecioInput = document.getElementById("habitacionPrecio");


    let reservafechaActual = new Date();





    function convertirFechaAStringReserva(fechaInput) {
        let fecha = new Date(fechaInput);
        let year = fecha.getFullYear();
        let month = String(fecha.getMonth() + 1).padStart(2, "0");
        let day = String(fecha.getDate()).padStart(2, "0");
        let hour = fecha.getHours();
        let minute = String(fecha.getMinutes()).padStart(2, "0");
        let meridiano = hour >= 12 ? "PM" : "AM";

        // Convertir a formato 12 horas
        hour = hour % 12 || 12; // 12 AM y 12 PM deben mostrarse correctamente

        return `${year}-${month}-${day} ${String(hour).padStart(2, "0")}:${minute} ${meridiano}`;
    }

    function formatearFechaParaInput(date) {
        let year = date.getFullYear();
        let month = String(date.getMonth() + 1).padStart(2, "0"); // Meses van de 0-11
        let day = String(date.getDate()).padStart(2, "0");
        let hours = String(date.getHours()).padStart(2, "0");
        let minutes = String(date.getMinutes()).padStart(2, "0");

        return `${year}-${month}-${day}T${hours}:${minutes}`;
    }

    // **(1) La fecha de entrada vacía tomará la del resumen o la actual**
    if (reservafechaEntradaResumen) {
        if (!reservafechaEntradaResumen.value) {
            reservafechaEntradaResumen.value = formatearFechaParaInput(reservafechaActual);
        } else {
            reservafechaEntradaResumen.value = reservaconvertirFechaParaInput(reservafechaEntradaResumen.value);
        }

        // Inicializar
        reservaactualizarFilas();
    }

    function reservaconvertirFechaParaInput(dateString) {
        let [fecha, hora, meridiano] = dateString.split(" ");
        let [year, month, day] = fecha.split("-").map(num => num.padStart(2, "0"));
        let [hour, minute] = hora.split(":").map(num => num.padStart(2, "0"));

        // Convertir a 24 horas si es necesario
        if (meridiano === "PM" && hour !== "12") {
            hour = String(parseInt(hour) + 12);
        } else if (meridiano === "AM" && hour === "12") {
            hour = "00"; // Medianoche
        }

        return `${year}-${month}-${day}T${hour}:${minute}`;
    }

    // Formatear fecha a 'YYYY-MM-DD hh:mm A'
    function reservafechaParseString(fecha) {
        const opcionesFecha = { year: 'numeric', month: '2-digit', day: '2-digit' };
        const opcionesHora = { hour: '2-digit', minute: '2-digit', hour12: true };
        const fechaFormateada = fecha.toLocaleDateString('es-ES', opcionesFecha).split('/').reverse().join('-');
        const horaFormateada = fecha.toLocaleTimeString('es-ES', opcionesHora).replace('.', '.');

        return `${fechaFormateada} ${horaFormateada}`;
    }

    function reservafechaParseStringDiaHora(fecha) {
        const opcionesFecha = { day: '2-digit' };
        const opcionesHora = { hour: '2-digit', minute: '2-digit', hour12: true };
        const fechaFormateada = fecha.toLocaleDateString('es-ES', opcionesFecha);
        const horaFormateada = fecha.toLocaleTimeString('es-ES', opcionesHora).replace('.', '.');

        return `| ${fechaFormateada} | ${horaFormateada}`;
    }

    function reservastringParseFecha(fechaStr) {
        let [fecha, hora] = fechaStr.split(" ");
        let [year, month, day] = fecha.split("-").map(num => parseInt(num));
        let [hour, minute] = hora.split(":").map(num => parseInt(num));
        let meridiano = hora.includes("AM") ? "AM" : "PM";

        // Ajustar la hora según AM/PM
        if (meridiano === "PM" && hour < 12) {
            hour += 12;  // Convertir a 24 horas
        } else if (meridiano === "AM" && hour === 12) {
            hour = 0; // Convertir medianoche a 00:00
        }

        return new Date(year, month - 1, day, hour, minute);
    }


    // **(4) Calcular fecha de salida**
    function reservacalcularFechaSalida(fecha, tipoServicio) {
        let fechaSalida = new Date(fecha);

        if (tipoServicio.includes("COMPLETO")) {
            fechaSalida.setDate(fechaSalida.getDate() + 1);
            fechaSalida.setHours(12, 0, 0); // Medio día del día siguiente
        } else {
            fechaSalida.setHours(18, 0, 0); // 6 PM del mismo día
        }
        return reservafechaParseString(fechaSalida);
    }

    // **(2) Manejar el cambio de días de alojamiento**
    function reservaactualizarFilas() {
        const dias = parseInt(reservadiasAlojamientoResumen.value) || 1;
        reservatablaFechasBody.innerHTML = ""; // Limpiar la tabla antes de reconstruirla

        let fechaBase = new Date(reservastringParseFecha(convertirFechaAStringReserva(reservafechaEntradaResumen.value)));

        for (let i = 0; i < dias; i++) {
            let nuevaFila = document.createElement("tr");
            let fechaEntradaStr = reservafechaParseStringDiaHora(fechaBase);
            let tipo_servicio = reservaselectServicio.options[reservaselectServicio.selectedIndex];
            let fechaSalidaStr = reservacalcularFechaSalida(new Date(fechaBase), tipo_servicio.getAttribute("data-tipo"));
            let fechaSalidaStrDiaHora = reservafechaParseStringDiaHora(reservastringParseFecha(fechaSalidaStr));

            nuevaFila.innerHTML = `
                            <td class="align-middle text-center">
                                <span class="bg-success-subtle rounded-4 p-2">${fechaEntradaStr}</span>
                            </td>
                            <td class="align-middle text-center">
                                <span class="bg-secondary-subtle rounded-4 p-2">
                                    ${tipo_servicio.getAttribute("data-tipo").includes("COMPLETO") ? "COMPLETO" : "MEDIO DÍA"}
                                </span>
                            </td>
                            <td class="align-middle text-center"><span class="bg-danger-subtle rounded-4 p-2">${fechaSalidaStrDiaHora}</span></td>
                            <td class="align-middle text-center"><span class="mx-3">${'S/. ' + reservacalcularImporte()}</span></td>
                        `;
            reservatablaFechasBody.appendChild(nuevaFila);

            fechaBase = new Date(reservastringParseFecha(fechaSalidaStr));
        }

        reservaactualizarResumen();
    }

    // **(3) Calcular importe según tipo de servicio**
    function reservacalcularImporte() {
        let precioBase = parseFloat(reservaprecioInput.getAttribute("data-precio")) || 0;
        let tipo_servicio = reservaselectServicio.options[reservaselectServicio.selectedIndex];

        return tipo_servicio.getAttribute("data-tipo").includes("MEDIO") ? (precioBase / 2).toFixed(2) : precioBase.toFixed(2);
    }

    // **(5) Bloquear servicio si hay más de un día**
    function reservamanejarBloqueoServicio() {
        if (parseInt(reservadiasAlojamientoResumen.value) > 1) {
            reservaselectServicio.value = "COMPLETO";
            reservaselectServicio.setAttribute("disabled", true);
        } else {
            reservaselectServicio.removeAttribute("disabled");
        }
        reservaactualizarFilas();
    }

    // **(6) Calcular monto total**
    function reservacalcularMontoTotal() {
        let dias = parseInt(reservadiasAlojamientoResumen.value) || 1;
        let precioBase = reservacalcularImporte(parseFloat(reservaprecioInput.getAttribute("data-precio")) || 0);
        let descuento = parseFloat(reservadescuentoResumen.value) || 0;
        let adelanto = parseFloat(reservamontoAdelantoResumen.value) || 0;

        return ((dias * precioBase) - descuento - adelanto).toFixed(2);
    }

    // **(4) Calcular fecha de salida**
    function reservacalcularFechaSalidaParaDias(fecha, dias) {
        let fechaSalida = new Date(fecha);

        fechaSalida.setDate(fechaSalida.getDate() + dias);
        fechaSalida.setHours(12, 0, 0); // Medio día del día siguiente

        return fechaParseString(fechaSalida);
    }

    // **Actualizar el resumen final**
    function reservaactualizarResumen() {
        let fechaEntrada = reservastringParseFecha(convertirFechaAStringReserva(reservafechaEntradaResumen.value));
        let dias = parseInt(reservadiasAlojamientoResumen.value) || 1;
        let fechaSalida;

        if (dias > 1) {
            fechaSalida = reservacalcularFechaSalidaParaDias(fechaEntrada, dias);
        } else {
            fechaSalida = reservacalcularFechaSalida(fechaEntrada, reservaselectServicio.options[reservaselectServicio.selectedIndex].getAttribute("data-tipo"));
        }

        reservafechaSalidaResumen.value = fechaSalida;
        reservamontoTotalResumen.value = reservacalcularMontoTotal();
    }

    // **Eventos**
    if (reservafechaEntradaResumen) {
        reservafechaEntradaResumen.addEventListener("change", reservaactualizarFilas);
    }
    if (reservadiasAlojamientoResumen) {
        reservadiasAlojamientoResumen.addEventListener("input", reservamanejarBloqueoServicio);
    }
    if (reservaselectServicio) {
        reservaselectServicio.addEventListener("change", reservaactualizarFilas);
    }
    if (reservadescuentoResumen) {
        reservadescuentoResumen.addEventListener("input", reservaactualizarResumen);
    }
    if (reservamontoAdelantoResumen) {
        reservamontoAdelantoResumen.addEventListener("input", reservaactualizarResumen);
    }










    let selects = document.querySelectorAll(".estado-selector");

    if (selects) {
        selects.forEach(select => {
            let id = select.getAttribute("data-id");
            let btnGuardar = document.getElementById("guardar_" + id);
            let estadoInicial = select.value; // Guardamos el estado inicial

            // Detectar cambios en el estado
            select.addEventListener("change", function () {
                if (select.value !== estadoInicial) {
                    btnGuardar.removeAttribute("disabled");
                } else {
                    btnGuardar.setAttribute("disabled", "true");
                }
            });

            // Evento para guardar cambios al hacer clic en el botón
            btnGuardar.addEventListener("click", function () {
                let nuevoEstado = select.value;

                fetch(`/habitaciones/contenido/actualizar/${id}`, {
                    method: "POST",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify({ estado_caracteristica: nuevoEstado })
                })
                    .then(response => {
                        if (response.ok) {
                            console.log("Estado actualizado correctamente");
                            estadoInicial = nuevoEstado; // Actualizar estado inicial
                            btnGuardar.setAttribute("disabled", "true");
                        } else {
                            alert("Error al actualizar el estado");
                        }
                    })
                    .catch(error => console.error("Error:", error));
            });
        });
    }
});