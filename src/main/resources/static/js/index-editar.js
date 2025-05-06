document.addEventListener("DOMContentLoaded", function () {
    const inputDNI = document.getElementById("inputDNI");
    const inputNombres = document.getElementById("inputNombres");
    const inputApellidos = document.getElementById("inputApellidos");
    const inputEdad = document.getElementById("inputEdad");
    const inputCelular = document.getElementById("inputCelular");
    const tablaClientesBody = document.getElementById("tablaClientesBody");
    const idVentaInput = document.getElementById("idVenta");

    let clientesTemporales = []; // Clientes agregados y a agregar en esta sesión

    // Cargar los clientes ya registrados en la lista temporal
    if (idVentaInput && idVentaInput.value.trim() !== "") {
        document.querySelectorAll("#tablaClientesBody tr").forEach(row => {
            let dni = row.cells[0].innerText;
            let nombres = row.cells[1].innerText;
            let apellidos = row.cells[2].innerText;
            let edad = row.cells[3].innerText;
            let celular = row.cells[4].innerText;
            clientesTemporales.push({ dni, nombres, apellidos, edad, celular, eliminado: false });
        });

        bloquearBtnGuardarRegistro(clientesTemporales, "btnGuardar")
    }


    // Función para agregar un cliente temporalmente
    function agregarCliente() {
        const dni = inputDNI.value.trim();
        const nombres = inputNombres.value.trim().toUpperCase();
        const apellidos = inputApellidos.value.trim().toUpperCase();
        const edad = inputEdad.value.trim();
        const celular = inputCelular.value.trim();

        if (!dni || !nombres || !apellidos) {
            alert("Por favor, completa todos los campos del cliente.");
            return;
        }

        if (dni.length < 8 || isNaN(dni)) {
            alert("El DNI debe contener 8 números.");
            return;
        }

        if (!edad) edad = 0;
        if (!celular) celular = "999999999";

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
            clientesTemporales.push({ dni, nombres, apellidos, edad, celular, eliminado: false });
        }

        const fila = document.createElement("tr");
        fila.innerHTML = `
                    <td class="border align-middle">${dni}</td>
                    <td class="align-middle">${nombres}</td>
                    <td class="align-middle">${apellidos}</td>
                    <td class="align-middle">${edad}</td>
                    <td class="align-middle">${celular}</td>
                    <td class="text-center align-middle border">
                        <button type="button" class="btn btn-danger btn-sm" onclick="eliminarCliente(this, '${dni}')">X</button>
                    </td>
                `;
        tablaClientesBody.appendChild(fila);

        inputDNI.value = "";
        inputNombres.value = "";
        inputApellidos.value = "";
        inputEdad.value = "";
        inputCelular.value = "";

        bloquearBtnGuardarRegistro(clientesTemporales, "btnGuardar");
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

        bloquearBtnGuardarRegistro(clientesTemporales, "btnGuardar");
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
    const resumenMontoTotalValor = document.getElementById("montoTotalResumenValor");

    const precioInput = document.getElementById("habitacionPrecio");
    const tablaFechasBody = document.getElementById("tableBodyFechasAlojamiento");

    const fechaActual = new Date();
    let fechaEntradaValor;

    // **(1) La fecha de entrada vacía tomará la del resumen o la actual**
    if (idVentaInput) {
        if (idVentaInput.value !== null && idVentaInput.value.trim() !== "") {
            fechaEntradaValor = new Date(resumenFechaEntradaValor.value);

            resumenFechaEntrada.value = fechaParseString(fechaEntradaValor);
            resumenFechaSalida.value = fechaParseString(new Date(resumenFechaSalidaValor.value));
        } else {
            fechaEntradaValor = new Date(fechaActual);
            resumenFechaEntrada.value = fechaParseString(fechaEntradaValor);
        }

        manejarBloqueoServicio();
    }

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

    // **(2) Manejar el cambio de días de alojamiento**
    function actualizarFilas() {
        const dias = parseInt(resumenDiasAlojamiento.value) || 1;
        tablaFechasBody.innerHTML = ""; // Limpiar la tabla antes de reconstruirla
        let tipo_servicio = resumenSelectServicio.options[resumenSelectServicio.selectedIndex];

        let fechaBase = new Date(fechaEntradaValor);

        for (let i = 1; i <= dias; i++) {
            let nuevaFila = document.createElement("tr");
            let servicio = (i === dias && tipo_servicio.getAttribute("data-tipo") === "MEDIO") ? "MEDIO" : "COMPLETO";

            let fechaSalida = calcularFechaSalida(fechaBase, servicio, fechaEntradaValor);

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
                            <td class="align-middle text-center"><span class="mx-3">${'S/. ' + calcularImporteEnFilas(servicio, precioInput)}</span></td>
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
            resumenFechaSalidaValor.value = fechaJsToJavaParseString(calcularFechaSalida(fechaBase, "COMPLETO", fechaEntradaValor));
            fechaSalida = fechaParseString(calcularFechaSalida(fechaBase, "COMPLETO", fechaEntradaValor));
        }

        resumenFechaSalida.value = fechaSalida;
        resumenMontoTotal.value = calcularMontoTotal(resumenSelectServicio, resumenDiasAlojamiento, resumenDescuento, resumenMontoAdelanto, precioInput);
        resumenMontoTotalValor.value = calcularMontoTotal(resumenSelectServicio, resumenDiasAlojamiento, resumenDescuento, 0, precioInput);

        if (parseFloat(resumenMontoTotal.value) <= 0.0) {
            resumenSelectEstado.value = "PAGADO";
            resumenSelectEstado.setAttribute("disabled", true);
        } else {
            resumenSelectEstado.value = "POR COBRAR";
            resumenSelectEstado.removeAttribute("disabled");
        }
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













    const reservainputDNI = document.getElementById("reservainputDNI");
    const reservainputNombres = document.getElementById("reservainputNombres");
    const reservainputApellidos = document.getElementById("reservainputApellidos");
    const reservainputEdad = document.getElementById("reservaInputEdad");
    const reservainputCelular = document.getElementById("reservaInputCelular");
    const reservatablaClientesBody = document.getElementById("reservatablaClientesBody");
    const reservaidVentaInput = document.getElementById("reservaidVenta");

    let reservaclientesTemporales = []; // Clientes agregados y a agregar en esta sesión

    // Cargar los clientes ya registrados en la lista temporal
    if (reservaidVentaInput && reservaidVentaInput.value.trim() !== "") {
        document.querySelectorAll("#reservatablaClientesBody tr").forEach(row => {
            let dni = row.cells[0].innerText;
            let nombres = row.cells[1].innerText;
            let apellidos = row.cells[2].innerText;
            let edad = row.cells[3].innerText;
            let celular = row.cells[4].innerText;
            reservaclientesTemporales.push({ dni, nombres, apellidos, edad, celular, eliminado: false });
        });
    }
    bloquearBtnGuardarRegistro(reservaclientesTemporales, "reservabtnGuardar");

    // Función para agregar un cliente temporalmente
    function reservaagregarCliente() {
        const dni = reservainputDNI.value.trim();
        const nombres = reservainputNombres.value.trim().toUpperCase();
        const apellidos = reservainputApellidos.value.trim().toUpperCase();
        const edad = reservainputEdad.value.trim();
        const celular = reservainputCelular.value.trim();

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
            reservaclientesTemporales.push({ dni, nombres, apellidos, edad, celular, eliminado: false });
        }

        const fila = document.createElement("tr");
        fila.innerHTML = `
                    <td class="border align-middle">${dni}</td>
                    <td class="align-middle">${nombres}</td>
                    <td class="align-middle">${apellidos}</td>
                    <td class="align-middle">${edad}</td>
                    <td class="align-middle">${celular}</td>
                    <td class="text-center align-middle border">
                        <button type="button" class="btn btn-danger btn-sm" onclick="reservaeliminarCliente(this, '${dni}')">X</button>
                    </td>
                `;
        reservatablaClientesBody.appendChild(fila);

        reservainputDNI.value = "";
        reservainputNombres.value = "";
        reservainputApellidos.value = "";
        reservainputEdad.value = "";
        reservainputCelular.value = "";

        bloquearBtnGuardarRegistro(reservaclientesTemporales, "reservabtnGuardar");
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
        bloquearBtnGuardarRegistro(reservaclientesTemporales, "reservabtnGuardar");
    };

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
                alert("Error al guardar la reserva");
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





    // // FECHAS EN SERVICIO DE ALOJAMIENTO
    const reservaResumenFechaEntrada = document.getElementById("reservafechaEntradaResumen");
    const reservaResumenFechaEntradaValor = document.getElementById("reservafechaEntradaResumenValor");
    const reservaResumenFechaSalida = document.getElementById("reservafechaSalidaResumen");
    const reservaResumenFechaSalidaValor = document.getElementById("reservafechaSalidaResumenValor");
    const reservaResumenDiasAlojamiento = document.getElementById("reservatiempoEstadiaResumen");

    const reservaResumenSelectServicio = document.getElementById("reservaselectTipoServicioResumen");
    const reservaResumenSelectEstado = document.getElementById("reservaselectEstadoResumen");

    const reservaResumenDescuento = document.getElementById("reservadescuentoResumen");
    const reservaResumenMontoAdelanto = document.getElementById("reservamontoAdelantoResumen");
    const reservaResumenMontoTotal = document.getElementById("reservamontoTotalResumen");
    const reservaResumenMontoTotalValor = document.getElementById("reservamontoTotalResumenValor");

    const reservaPrecioInput = document.getElementById("habitacionPrecio");
    const reservaTablaFechasBody = document.getElementById("reservatableBodyFechasAlojamiento");

    let reservafechaActual = new Date();
    let reservaFechaEntradaValor;

    // **(1) La fecha de entrada vacía tomará la del resumen o la actual**
    if (reservaidVentaInput) {
        if (reservainputDNI.value !== null && reservainputDNI.value.trim() !== "") {
            reservaFechaEntradaValor = fechaJavaToJsToDatetimelocal(new Date(reservaResumenFechaEntradaValor.value));

            reservaResumenFechaEntrada.value = reservaFechaEntradaValor;
            reservaResumenFechaSalida.value = fechaParseString(new Date(reservaResumenFechaSalidaValor.value));
        } else {
            reservaFechaEntradaValor = fechaJavaToJsToDatetimelocal(new Date(reservafechaActual));
            reservaResumenFechaEntrada.value = reservaFechaEntradaValor;
        }

        reservamanejarBloqueoServicio();
    }

    // **(5) Bloquear servicio si hay más de un día**
    function reservamanejarBloqueoServicio() {
        if (parseInt(reservaResumenDiasAlojamiento.value) < 2) {
            reservaResumenSelectServicio.value = "COMPLETO";
            reservaResumenSelectServicio.setAttribute("disabled", true);
        } else {
            reservaResumenSelectServicio.removeAttribute("disabled");
        }

        reservaactualizarFilas();
    }

    // **(2) Manejar el cambio de días de alojamiento**
    function reservaactualizarFilas() {
        const dias = parseInt(reservaResumenDiasAlojamiento.value) || 1;
        reservaTablaFechasBody.innerHTML = ""; // Limpiar la tabla antes de reconstruirla
        let tipo_servicio = reservaResumenSelectServicio.options[reservaResumenSelectServicio.selectedIndex];

        let fechaBase = new Date(reservaResumenFechaEntrada.value);

        for (let i = 1; i <= dias; i++) {
            let nuevaFila = document.createElement("tr");
            let servicio = (i === dias && tipo_servicio.getAttribute("data-tipo") === "MEDIO") ? "MEDIO" : "COMPLETO";

            let fechaSalida = calcularFechaSalida(fechaBase, servicio, reservaFechaEntradaValor);

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
                            <td class="align-middle text-center"><span class="mx-3">${'S/. ' + calcularImporteEnFilas(servicio, reservaPrecioInput)}</span></td>
                        `;

            reservaTablaFechasBody.appendChild(nuevaFila);

            fechaBase = new Date(fechaSalida);
        }

        reservaactualizarResumen();
    }

    // **Actualizar el resumen final**
    function reservaactualizarResumen() {
        let tipo_servicio = reservaResumenSelectServicio.options[reservaResumenSelectServicio.selectedIndex];
        let fechaEntrada = new Date(reservaResumenFechaEntrada.value);

        let fechaBase = new Date(fechaEntrada);
        let fechaSalida = "";

        let dias = parseInt(reservaResumenDiasAlojamiento.value) || 1;

        reservaResumenFechaEntradaValor.value = fechaJsToJavaParseString(new Date(fechaEntrada));

        if (dias > 1) {
            if (tipo_servicio.getAttribute("data-tipo") == "COMPLETO") {
                fechaBase.setDate(fechaBase.getDate() + dias);
                fechaBase.setHours(12, 0, 0); // Medio día
            } else {
                fechaBase.setDate(fechaBase.getDate() + dias);
                fechaBase.setHours(18, 0, 0); // 6 PM
            }

            reservaResumenFechaSalidaValor.value = fechaJsToJavaParseString(fechaBase);
            fechaSalida = fechaParseString(fechaBase);
        } else if (dias === 1) {
            reservaResumenFechaSalidaValor.value = fechaJsToJavaParseString(calcularFechaSalida(fechaBase, "COMPLETO", reservaFechaEntradaValor));
            fechaSalida = fechaParseString(calcularFechaSalida(fechaBase, "COMPLETO", new Date(reservaResumenFechaEntrada.value)));
        } else {
            alert("La cantidad de días de alojamiento no puede ser menor a 1.");
        }

        reservaResumenFechaSalida.value = fechaSalida;
        reservaResumenMontoTotal.value = calcularMontoTotal(reservaResumenSelectServicio, reservaResumenDiasAlojamiento, reservaResumenDescuento, reservaResumenMontoAdelanto, reservaPrecioInput);
        reservaResumenMontoTotalValor.value = calcularMontoTotal(reservaResumenSelectServicio, reservaResumenDiasAlojamiento, reservaResumenDescuento, 0, reservaPrecioInput);

        if (parseFloat(reservaResumenMontoTotal.value) <= 0.0) {
            reservaResumenSelectEstado.value = "PAGADO";
            reservaResumenSelectEstado.setAttribute("disabled", true);
        } else {
            reservaResumenSelectEstado.value = "POR COBRAR";
            reservaResumenSelectEstado.removeAttribute("disabled");
        }
    }

    // **Eventos**
    if (reservaResumenFechaEntrada) {
        reservaResumenFechaEntrada.addEventListener("change", reservaactualizarFilas);
    }
    if (reservaResumenDiasAlojamiento) {
        reservaResumenDiasAlojamiento.addEventListener("input", reservamanejarBloqueoServicio);
    }
    if (reservaResumenSelectServicio) {
        reservaResumenSelectServicio.addEventListener("change", reservaactualizarFilas);
    }
    if (reservaResumenDescuento) {
        reservaResumenDescuento.addEventListener("input", reservaactualizarResumen);
    }
    if (reservaResumenMontoAdelanto) {
        reservaResumenMontoAdelanto.addEventListener("input", reservaactualizarResumen);
    }




    // FUNCIONES PARA FECHAS
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
    // Convertir fecha JS a String en formato Java
    function fechaJsToJavaParseString(fecha) {
        const anio = fecha.getFullYear();
        const mes = String(fecha.getMonth() + 1).padStart(2, '0');
        const dia = String(fecha.getDate()).padStart(2, '0');
        const horas = String(fecha.getHours()).padStart(2, '0');
        const minutos = String(fecha.getMinutes()).padStart(2, '0');
        const segundos = String(fecha.getSeconds()).padStart(2, '0');

        return `${anio}-${mes}-${dia}T${horas}:${minutos}:${segundos}`;
    }
    // Convertir fecha Java a JS para enviar a datetime-local de HTML
    function fechaJavaToJsToDatetimelocal(date) {
        let year = date.getFullYear();
        let month = String(date.getMonth() + 1).padStart(2, "0"); // Meses van de 0-11
        let day = String(date.getDate()).padStart(2, "0");
        let hours = String(date.getHours()).padStart(2, "0");
        let minutes = String(date.getMinutes()).padStart(2, "0");

        return `${year}-${month}-${day}T${hours}:${minutes}`;
    }


    // FUNCIONES PARA LA INFORMACIÓN
    // Buscar cliente por DNI en la base de datos, después en la API externa en caso de no encontrarlo en la base de datos
    function buscarCliente(dni, btn, nombres, apellidos, edad, celular) {
        if (dni < 8 || isNaN(dni)) {
            alert("El DNI debe contener 8 números.");
            return;
        }

        fetch(`/buscar-cliente?dni=${dni}`, {
            method: "GET"
        }).then(response => response.json()).then(data => {
            if (data.encontrado) {
                nombres.value = data.nombres;
                apellidos.value = data.apellidos;
                edad.value = data.edad;
                celular.value = data.celular;

                if (data.estado_vetado) {
                    // Mostrar modal con la razón del veto
                    document.getElementById('razonVeto').innerText = data.razon_vetado;
                    let modalVetado = new bootstrap.Modal(document.getElementById('modalVetado'));
                    modalVetado.show();

                    // Bloquear el registro del cliente
                    bloquearBtnAgregarCliente(btn);
                } else {
                    // Manejar el caso en que el cliente no exista
                    desbloquearBtnAgregarCliente(btn);
                }
            } else {
                console.log("Cliente no encontrado en la base de datos. Consultando API externa...");
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
                    alert("No se encontró información para este DNI. Agregar manualmente");
                    console.error("Error al realizar la consulta:", error);
                });
            }
        }).catch(error => console.error("Error buscando cliente:", error));
    }
    // Bloquear el botón de agregar cliente
    function bloquearBtnGuardarRegistro(listaClientes, idbtn) {
        btn = document.getElementById(idbtn);

        if (!btn) return;

        btn.disabled = listaClientes.length === 0;
    }
    // Calcula la fecha de salida según el tipo de servicio
    function calcularFechaSalida(fecha, tipoServicio, p_fechaEntradaValor) {
        let fechaSalida = new Date(fecha);
        p_fechaEntradaValor = new Date(p_fechaEntradaValor);

        if (tipoServicio.includes("COMPLETO")) {
            if (p_fechaEntradaValor.getHours() >= 6) {
                fechaSalida.setDate(fechaSalida.getDate() + 1); // Día siguiente
            }
            fechaSalida.setHours(12, 0, 0); // Medio día del mismo día
        } else {
            fechaSalida.setHours(18, 0, 0); // 6 PM del mismo día
        }

        return fechaSalida;
    }
    // Calcular importe según tipo de servicio
    function calcularImporteEnFilas(servicio, p_precioInput) {
        let precioBase = parseFloat(p_precioInput.getAttribute("data-precio")) || 0;

        return servicio == "MEDIO" ? (precioBase / 2).toFixed(2) : precioBase.toFixed(2);
    }
    // Calcular monto total
    function calcularMontoTotal(servicio, p_dias, p_descuento, p_adelanto, p_precioHab) {
        let tipo_servicio = servicio.options[servicio.selectedIndex];
        let dias = parseInt(p_dias.value) || 1;
        let descuento = parseFloat(p_descuento.value) || 0;
        let adelanto = parseFloat(p_adelanto.value) || 0;
        let precioBase = parseFloat(p_precioHab.getAttribute("data-precio")) || 0;

        return tipo_servicio.getAttribute("data-tipo").includes("MEDIO") ? ((precioBase * (dias - 1)) + (precioBase / 2) - descuento - adelanto).toFixed(2) : ((dias * precioBase) - descuento - adelanto).toFixed(2);
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

    document.addEventListener("click", function (event) {
        if (event.target.id === "btnRetirarCliente") {
            retirarCliente();
        }
        if (event.target.id === "btnAgregarCliente") {
            agregarCliente();
        }
        if (event.target.id === "btnBuscarDNI") {
            buscarCliente(inputDNI.value.trim(), "btnAgregarCliente", inputNombres, inputApellidos, inputEdad, inputCelular);
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
            buscarCliente(reservainputDNI.value.trim(), "reservabtnAgregarCliente", reservainputNombres, reservainputApellidos, reservainputEdad, reservainputCelular);
        }
        if (event.target.id === "reservabtnGuardar") {
            reservaguardarReserva(event);
        }
    });
});