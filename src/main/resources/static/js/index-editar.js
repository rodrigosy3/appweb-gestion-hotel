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
    }

    bloquearBtnGuardarRegistro(clientesTemporales, "btnGuardar")

    // Función para agregar un cliente temporalmente
    function agregarCliente() {
        const dni = inputDNI.value.trim();
        const nombres = inputNombres.value.trim().toUpperCase();
        const apellidos = inputApellidos.value.trim().toUpperCase();
        let edad = inputEdad.value.trim();
        let celular = inputCelular.value.trim();

        if (!dni || !nombres || !apellidos) {
            alert("Por favor, completa todos los campos del cliente.");
            return;
        }

        if (dni.length < 8 || isNaN(dni)) {
            alert("El DNI debe contener 8 números.");
            return;
        }

        if (!edad) edad = 0;
        if (!celular) celular = "000000000";

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
        const estado = resumenSelectEstado.value; // Referencia al select

        // Abrir el modal de Bootstrap
        const modalElement = document.getElementById("confirmacionModal");
        const modalBootstrap = new bootstrap.Modal(modalElement);

        // Referencias para los botones dentro del modal
        const btnSiImprimir = document.getElementById("btnSiImprimir");
        const btnGuardarSinImprimir = document.getElementById("btnGuardarSinImprimir");

        // === COMPORTAMIENTO DINÁMICO SEGÚN ESTADO ===
        if (estado === "POR COBRAR") {
            btnSiImprimir.style.display = "none"; // Ocultar botón imprimir
            btnGuardarSinImprimir.textContent = "Guardar"; // Cambiar texto
        } else {
            btnSiImprimir.style.display = "inline-block"; // Mostrar botón imprimir
            btnGuardarSinImprimir.textContent = "Guardar sin imprimir";
        }
        modalBootstrap.show();

        const inputOpcionImpresion = document.getElementById("opcionImpresion");
        const formVenta = document.getElementById("formVentaHabitacion");

        // Función “handler” que hará el envío por fetch:
        function enviarFormConOpcion(opcion) {
            // Opción elegida
            inputOpcionImpresion.value = opcion; // "SI_IMPRIMIR" o "GUARDAR_SIN_IMPRIMIR"

            // FormData (incluyendo clientesTemporales)
            let formData = new FormData(formVenta);
            formData.append("clientesTemporales", JSON.stringify(clientesTemporales));

            // fetch al action del form
            fetch(formVenta.action, {
                method: "POST",
                body: formData
            }).then(response => {
                if (!response.ok) {
                    alert("Error al guardar la venta");
                    // Podrías mostrar alerta o mensaje aquí
                }

                window.location.href = "/"; // Redirigir a la página principal
            }).catch(error => console.error("Error:", error));
        }

        // Manejadores a los botones del modal **sólo una vez**:
        //     — Si el usuario hace clic en “Sí, guardar e imprimir”:
        btnSiImprimir.onclick = function () {
            modalBootstrap.hide();                // Cerrar el modal
            enviarFormConOpcion("SI_IMPRIMIR");   // Enviamos con esa opción
        };
        //     — Si el usuario hace clic en “Guardar sin imprimir”:
        btnGuardarSinImprimir.onclick = function () {
            modalBootstrap.hide();
            enviarFormConOpcion("NO_IMPRIMIR");
        };
        //     — Si cierra con “Cancelar” (data-bs-dismiss), no hace nada más.
    };

    function retirarCliente() {
        const idVenta = idVentaInput.value;  // Obtiene el ID de la venta

        if (!idVenta) {
            alert("No se puede actualizar el estado sin una venta registrada.");
            return;
        }

        const confirmacion = confirm("¿Estás seguro de que deseas finalizar esta venta?");

        if (!confirmacion) {
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
    const resumenUltimaFecha = document.getElementById("ultimaFecha");

    const resumenSelectServicio = document.getElementById("selectTipoServicioResumen");
    const resumenSelectEstado = document.getElementById("selectEstadoResumen");

    const resumenDescuento = document.getElementById("descuentoResumen");
    const resumenMontoTotalCobrado = document.getElementById("montoTotalCobradoResumen");

    const resumenMontoTotal = document.getElementById("montoTotalResumen");
    const resumenMontoTotalValor = document.getElementById("montoTotalResumenValor");

    const resumenMontoHabitacion = document.getElementById("montoHabitacionResumen");
    const resumenVuelto = document.getElementById("montoVueltoResumen");

    const precioInput = document.getElementById("habitacionPrecio");
    const tablaFechasBody = document.getElementById("tableBodyFechasAlojamiento");

    const precio_habitacion = parseFloat(precioInput.getAttribute("data-precio") || 0);

    let fechaEntradaValor;
    let auxUltimaFecha = "";

    if (resumenSelectEstado !== null && resumenSelectEstado.value === "POR COBRAR") {
        resumenMontoTotalCobrado.value = 0;
    }
    if (!(resumenUltimaFecha.value === "" || resumenUltimaFecha.value === null)) {
        auxUltimaFecha = resumenUltimaFecha.value;
    }
    // La fecha de entrada vacía tomará la del resumen o la actual
    if (idVentaInput) {
        if (idVentaInput.value !== null && idVentaInput.value.trim() !== "") {
            fechaEntradaValor = new Date(resumenFechaEntradaValor.value);

            resumenFechaEntrada.value = fechaParseString(fechaEntradaValor);
            resumenFechaSalida.value = fechaParseString(new Date(resumenFechaSalidaValor.value));
        } else {
            fechaEntradaValor = new Date();
            resumenFechaEntrada.value = fechaParseString(fechaEntradaValor);
        }

        manejarBloqueoServicio(false);
    }

    // Bloquear servicio si hay más de un día
    function manejarBloqueoServicio(huboCambio) {
        let dias = parseInt(resumenDiasAlojamiento.value || 1);
        dias = dias < 0 ? (dias * -1) : (dias === 0) ? 1 : dias;
        resumenDiasAlojamiento.value = dias;

        if (dias < 2) {
            resumenSelectServicio.value = "COMPLETO";
            resumenSelectServicio.setAttribute("disabled", true);
        } else {
            resumenSelectServicio.removeAttribute("disabled");
        }

        actualizarResumen(huboCambio);
    }

    // Manejar el cambio de días de alojamiento
    function actualizarFilas() {
        const dias = parseInt(resumenDiasAlojamiento.value || 1);
        tablaFechasBody.innerHTML = ""; // Limpiar la tabla antes de reconstruirla
        let tipo_servicio = resumenSelectServicio.value;

        let fechaBase = new Date(fechaEntradaValor);

        for (let i = 1; i <= dias; i++) {
            let nuevaFila = document.createElement("tr");
            let servicio = (i === dias && tipo_servicio === "MEDIO") ? "MEDIO" : "COMPLETO";

            let fechaSalida = calcularFechaSalida(fechaBase, servicio, fechaEntradaValor, (i === 1));

            nuevaFila.innerHTML = `
                <td class="align-middle text-center py-2">
                    <span class="hotel-date-badge hotel-date-entrada">
                        <i class="bi bi-calendar-check me-1"></i>
                        ${fechaParseStringDiaHora(fechaBase)}
                    </span>
                </td>
                <td class="align-middle text-center py-2">
                    <span class="hotel-service-badge ${servicio === "COMPLETO" ? "hotel-service-completo" : "hotel-service-medio"}">
                        <i class="bi bi-clock me-1"></i>
                        ${servicio === "COMPLETO" ? "COMPLETO" : "MEDIO DÍA"}
                    </span>
                </td>
                <td class="align-middle text-center py-2">
                    <span class="hotel-date-badge hotel-date-salida">
                        <i class="bi bi-calendar-x me-1"></i>
                        ${fechaParseStringDiaHora(fechaSalida)}
                    </span>
                </td>
                <td class="align-middle text-center py-2">
                    <span class="hotel-price-badge">
                        ${'S/. ' + calcularImporteEnFilas(servicio, precioInput)}
                    </span>
                </td>
            `;

            tablaFechasBody.appendChild(nuevaFila);
            fechaBase = new Date(fechaSalida);
        }
    }

    function actualizarResumen(cambiosEnServicio) {
        let servicio = resumenSelectServicio.value;

        let dias = parseInt(resumenDiasAlojamiento.value || 1);
        dias = dias < 0 ? (dias * -1) : (dias === 0) ? 1 : dias;
        resumenDiasAlojamiento.value = dias;

        let fechaBase = new Date(fechaEntradaValor);
        let madrugada = fechaBase.getHours() <= 6 ? 1 : 0; // Si es madrugada, no cuenta el día completo

        resumenFechaEntradaValor.value = fechaJsToJavaParseString(new Date(fechaEntradaValor));

        if (dias > 1) {
            if (servicio === "COMPLETO") {
                fechaBase.setDate(fechaBase.getDate() + dias - madrugada);
                fechaBase.setHours(12, 0, 0); // Medio día
            } else {
                fechaBase.setDate(fechaBase.getDate() + dias - madrugada - 1);
                fechaBase.setHours(18, 0, 0); // 6 PM
            }
        } else {
            fechaBase.setDate(fechaBase.getDate() + dias - madrugada);
            fechaBase.setHours(12, 0, 0); // Medio día
        }

        resumenFechaSalida.value = fechaParseString(fechaBase);
        resumenFechaSalidaValor.value = fechaJsToJavaParseString(fechaBase);
        resumenMontoTotal.value = calcularMontoTotal(resumenSelectServicio, resumenDiasAlojamiento, 0, 0, precioInput);
        resumenMontoTotalValor.value = calcularMontoTotal(resumenSelectServicio, resumenDiasAlojamiento, 0, 0, precioInput);

        let fHoy = new Date();
        let fSalida = new Date(fechaBase);
        let esMismoDia = (fHoy.getDate() === fSalida.getDate()) && (fHoy.getMonth() === fSalida.getMonth()) && (fHoy.getFullYear() === fSalida.getFullYear());
        let precio_estado = esMismoDia && (servicio === "MEDIO") ? (precio_habitacion / 2) : precio_habitacion;

        let descuento = parseFloat(resumenDescuento.value) || 0;
        let totalCobrado = parseFloat(resumenMontoTotalCobrado.value) || 0;
        let vuelto = 0;

        if (cambiosEnServicio) {
            totalCobrado = 0;
            vuelto = -1;
        } else {
            totalCobrado = totalCobrado < 0 ? (totalCobrado * -1) : totalCobrado;
            vuelto = totalCobrado - precio_estado;
        }

        resumenDescuento.value = (descuento).toFixed(2);
        resumenMontoTotalCobrado.value = totalCobrado;
        resumenMontoHabitacion.value = precio_estado.toFixed(2);

        if (vuelto < 0) {
            resumenVuelto.value = (0).toFixed(2);
            resumenVuelto.classList.remove("bg-success-subtle", "fw-bold");

            resumenSelectEstado.value = "POR COBRAR";
            resumenSelectEstado.style.backgroundColor = "#f8d7da"; // rojo claro
            resumenMontoTotalCobrado.style.backgroundColor = "#f8d7da";
        } else {
            resumenVuelto.value = vuelto.toFixed(2);
            resumenVuelto.classList.add("bg-success-subtle", "fw-bold");

            resumenSelectEstado.value = "PAGADO";
            resumenSelectEstado.style.backgroundColor = "#d4edda"; // verde claro
            resumenMontoTotalCobrado.style.backgroundColor = "#d4edda";
        }

        let nuevaUltimaFecha;

        if (resumenUltimaFecha.value === "") {
            // Primer cobro
            nuevaUltimaFecha = new Date(fechaEntradaValor);
        } else {
            // Ya hubo cobros: sumamos un día desde la última fecha
            nuevaUltimaFecha = new Date(resumenUltimaFecha.value);
            nuevaUltimaFecha.setDate(nuevaUltimaFecha.getDate() + 1);
            // Establecemos la hora
            nuevaUltimaFecha.setHours(12, 0, 0); // Mediodía
        }

        let fUltimaFecha = new Date(resumenUltimaFecha.value);
        let esDiaCobrado = false;

        if ((fHoy.getDate() >= fUltimaFecha.getDate()) && (fHoy.getDate() <= nuevaUltimaFecha.getDate())) {
            esDiaCobrado = true;
            if (fHoy.getDate() === nuevaUltimaFecha.getDate() && fHoy.getHours() >= nuevaUltimaFecha.getHours()) {
                esDiaCobrado = false;
            }
        }

        console.log('fUltimaFecha.getDate() :>> ', fUltimaFecha.getDate(), '   fHoy.getDate() :>> ', fHoy.getDate(), '   nuevaUltimaFecha.getDate() :>> ', nuevaUltimaFecha.getDate());
        console.log('fUltimaFecha.getHours() :>> ', fUltimaFecha.getHours(), '   fHoy.getHours() :>> ', fHoy.getHours(), '   nuevaUltimaFecha.getHours() :>> ', nuevaUltimaFecha.getHours());
        console.log('fUltimaFecha.getMinutes() :>> ', fUltimaFecha.getMinutes(), '   fHoy.getMinutes() :>> ', fHoy.getMinutes(), '   nuevaUltimaFecha.getMinutes() :>> ', nuevaUltimaFecha.getMinutes());
        console.log('esDiaCobrado :>> ', esDiaCobrado);

        // Si está PAGADO, guardamos como nueva fecha
        if (resumenSelectEstado.value === "PAGADO" && !esDiaCobrado) {
            resumenUltimaFecha.value = fechaJsToJavaParseString(nuevaUltimaFecha);
        } else {
            resumenUltimaFecha.value = auxUltimaFecha; // Mantienes el anterior si no se pagó
        }

        // let auxFecha = new Date(fechaEntradaValor);
        // let hoy = new Date();
        // let diaTransucrridos = parseInt(auxFecha.getDate() - hoy.getDate());

        // auxFecha.setDate(fechaEntradaValor.getDate() + diaTransucrridos - madrugada);
        // auxFecha.setHours(12, 0, 0);

        // if (resumenUltimaFecha.value === "") {
        //     resumenUltimaFecha.value = fechaJsToJavaParseString(auxFecha);
        //     console.log('resumenUltimaFecha.value :>> ', resumenUltimaFecha.value);
        // } else {
        //     if (resumenSelectEstado.value === "PAGADO") {
        //         resumenUltimaFecha.value = fechaJsToJavaParseString(auxFecha);
        //     } else {
        //         resumenUltimaFecha.value = auxUltimaFecha;
        //     }
        // }

        actualizarFilas();
    }

    // **Eventos**
    function actualizarSelect() {
        let estado = resumenSelectEstado.options[resumenSelectEstado.selectedIndex];
        resumenDescuento.value = (0).toFixed(2);

        if (estado.value === "PAGADO") {
            resumenMontoTotalCobrado.value = parseFloat(resumenMontoHabitacion.value);
            resumenSelectEstado.style.backgroundColor = "#d4edda"; // verde claro
        } else {
            resumenMontoTotalCobrado.value = 0;
            resumenSelectEstado.style.backgroundColor = "#f8d7da"; // rojo claro
        }

        actualizarResumen(false);
    }

    // if (resumenFechaEntrada) {
    //     resumenFechaEntrada.addEventListener("change", actualizarFilas);
    // }
    if (resumenDiasAlojamiento) {
        resumenDiasAlojamiento.addEventListener("input", () => manejarBloqueoServicio(true));
    }
    if (resumenSelectServicio) {
        resumenSelectServicio.addEventListener("change", () => actualizarResumen(true));
    }
    if (resumenDescuento) {
        resumenDescuento.addEventListener("input", () => actualizarResumen(false));
    }
    if (resumenMontoTotalCobrado) {
        resumenMontoTotalCobrado.addEventListener("change", () => actualizarResumen(false));
    }
    if (resumenSelectEstado) {
        resumenSelectEstado.addEventListener("change", actualizarSelect);
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

        if (!edad) edad.value = 0;
        if (!celular) celular.value = "999999999";

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
        const estado = reservaResumenSelectEstado.value; // Referencia al select

        // Abrir el modal de Bootstrap
        const modalElement = document.getElementById("confirmacionModal");
        const modalBootstrap = new bootstrap.Modal(modalElement);

        // Referencias para los botones dentro del modal
        const btnSiImprimir = document.getElementById("btnSiImprimir");
        const btnGuardarSinImprimir = document.getElementById("btnGuardarSinImprimir");

        // === COMPORTAMIENTO DINÁMICO SEGÚN ESTADO ===
        if (estado === "POR COBRAR") {
            btnSiImprimir.style.display = "none"; // Ocultar botón imprimir
            btnGuardarSinImprimir.textContent = "Guardar"; // Cambiar texto
        } else {
            btnSiImprimir.style.display = "inline-block"; // Mostrar botón imprimir
            btnGuardarSinImprimir.textContent = "Guardar sin imprimir";
        }
        modalBootstrap.show();

        const inputOpcionImpresion = document.getElementById("reservaOpcionImpresion");
        let form = document.getElementById("reservaformVentaHabitacion");

        function reservaEnviarFormConOpcion(opcion) {
            // Opción elegida
            inputOpcionImpresion.value = opcion; // "SI_IMPRIMIR" o "NO_IMPRIMIR"

            // FormData (incluyendo reservaclientesTemporales)
            let formData = new FormData(form);
            formData.append("reservaclientesTemporales", JSON.stringify(reservaclientesTemporales));

            // fetch al action del form
            fetch(form.action, {
                method: "POST",
                body: formData
            }).then(response => {
                if (!response.ok) {
                    alert("Error al guardar la reserva");
                    // Podrías mostrar alerta o mensaje aquí
                }

                window.location.href = "/"; // Redirigir a la página principal
            }).catch(error => console.error("Error:", error));
        }

        // Manejadores a los botones del modal **sólo una vez**:
        //     — Si el usuario hace clic en “Sí, guardar e imprimir”:
        btnSiImprimir.onclick = function () {
            modalBootstrap.hide();                // Cerrar el modal
            reservaEnviarFormConOpcion("SI_IMPRIMIR");   // Enviamos con esa opción
        };
        //     — Si el usuario hace clic en “Guardar sin imprimir”:
        btnGuardarSinImprimir.onclick = function () {
            modalBootstrap.hide();
            reservaEnviarFormConOpcion("NO_IMPRIMIR");
        };
        //     — Si cierra con “Cancelar” (data-bs-dismiss), no hace nada más.
    };

    function reservaEliminarReserva() {
        const idVentaReservada = reservaidVentaInput.value;
        console.log(idVentaReservada);
        if (!idVentaReservada) {
            console.log("No se puede cancelar una reserva no registrada.");
            return;
        }

        const confirmacion = confirm("¿Estás seguro de que deseas cancelar esta reserva?");

        if (!confirmacion) {
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

        const confirmacion = confirm("¿Estás seguro de que deseas habilitar esta reserva?");
        if (!confirmacion) {
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
    const reservaUltimaFecha = document.getElementById("reservaultimaFecha");

    const reservaResumenSelectServicio = document.getElementById("reservaselectTipoServicioResumen");
    const reservaResumenSelectEstado = document.getElementById("reservaselectEstadoResumen");

    const reservaResumenDescuento = document.getElementById("reservadescuentoResumen");
    // const reservaResumenMontoTotalCobrado = document.getElementById("reservamontoTotalCobradoResumen");

    const reservaResumenMontoTotal = document.getElementById("reservamontoTotalResumen");
    const reservaResumenMontoTotalValor = document.getElementById("reservamontoTotalResumenValor");

    const reservaResumenMontoHabitacion = document.getElementById("reservamontoHabitacionResumen");
    // const reservaResumenVuelto = document.getElementById("reservamontoVueltoResumen");

    const reservaPrecioInput = document.getElementById("habitacionPrecio");
    const reservaTablaFechasBody = document.getElementById("reservatableBodyFechasAlojamiento");

    const reserva_precio_habitacion = parseFloat(reservaPrecioInput.getAttribute("data-precio") || 0);

    let reservaFechaEntradaValor;

    // La fecha de entrada vacía tomará la del resumen o la actual
    if (reservaidVentaInput) {
        if (reservainputDNI.value !== null && reservainputDNI.value.trim() !== "") {
            reservaFechaEntradaValor = fechaJavaToJsToDatetimelocal(new Date(reservaResumenFechaEntradaValor.value));

            reservaResumenFechaEntrada.value = reservaFechaEntradaValor;
            reservaResumenFechaSalida.value = fechaParseString(new Date(reservaResumenFechaSalidaValor.value));
        } else {
            reservaFechaEntradaValor = fechaJavaToJsToDatetimelocal(new Date());
            reservaResumenFechaEntrada.value = reservaFechaEntradaValor;
        }

        reservamanejarBloqueoServicio();
    }

    // Bloquear servicio si hay más de un día
    function reservamanejarBloqueoServicio() {
        let dias = parseInt(reservaResumenDiasAlojamiento.value || 1);
        dias = dias < 0 ? (dias * -1) : (dias === 0) ? 1 : dias;
        reservaResumenDiasAlojamiento.value = dias;

        if (dias < 2) {
            reservaResumenSelectServicio.value = "COMPLETO";
            reservaResumenSelectServicio.setAttribute("disabled", true);
        } else {
            reservaResumenSelectServicio.removeAttribute("disabled");
        }

        reservaactualizarResumen();
    }

    // Manejar el cambio de días de alojamiento
    function reservaactualizarFilas() {
        const dias = parseInt(reservaResumenDiasAlojamiento.value || 1);
        reservaTablaFechasBody.innerHTML = ""; // Limpiar la tabla antes de reconstruirla
        let tipo_servicio = reservaResumenSelectServicio.value;

        let fechaBase = new Date(reservaResumenFechaEntrada.value);

        for (let i = 1; i <= dias; i++) {
            let nuevaFila = document.createElement("tr");
            let servicio = (i === dias && tipo_servicio === "MEDIO") ? "MEDIO" : "COMPLETO";

            let fechaSalida = calcularFechaSalida(fechaBase, servicio, fechaBase, (i === 1));

            nuevaFila.innerHTML = `
                            <td class="align-middle text-center py-2">
                                <span class="hotel-date-badge hotel-date-entrada">
                                    <i class="bi bi-calendar-check me-1"></i>
                                    ${fechaParseStringDiaHora(fechaBase)}
                                </span>
                            </td>
                            <td class="align-middle text-center py-2">
                                <span class="hotel-service-badge ${servicio === "COMPLETO" ? "hotel-service-completo" : "hotel-service-medio"}">
                                    <i class="bi bi-clock me-1"></i>
                                    ${servicio === "COMPLETO" ? "COMPLETO" : "MEDIO DÍA"}
                                </span>
                            </td>
                            <td class="align-middle text-center py-2">
                                <span class="hotel-date-badge hotel-date-salida">
                                    <i class="bi bi-calendar-x me-1"></i>
                                    ${fechaParseStringDiaHora(fechaSalida)}
                                </span>
                            </td>
                            <td class="align-middle text-center py-2">
                                <span class="hotel-price-badge">
                                ${'S/. ' + calcularImporteEnFilas(servicio, reservaPrecioInput)}
                                </span>
                            </td>
                        `;

            reservaTablaFechasBody.appendChild(nuevaFila);
            fechaBase = new Date(fechaSalida);
        }
    }

    // **Actualizar el resumen final**
    function reservaactualizarResumen() {
        let servicio = reservaResumenSelectServicio.value;

        let dias = parseInt(reservaResumenDiasAlojamiento.value || 1);
        dias = dias < 0 ? (dias * -1) : (dias === 0) ? 1 : dias;
        reservaResumenDiasAlojamiento.value = dias;

        let fechaBase = new Date(reservaResumenFechaEntrada.value);
        let madrugada = fechaBase.getHours() <= 6 ? 1 : 0; // Si es madrugada, no cuenta el día completo

        reservaResumenFechaEntradaValor.value = fechaJsToJavaParseString(new Date(fechaBase));

        if (dias > 1) {
            if (servicio == "COMPLETO") {
                fechaBase.setDate(fechaBase.getDate() + dias - madrugada);
                fechaBase.setHours(12, 0, 0); // Medio día
            } else {
                fechaBase.setDate(fechaBase.getDate() + dias - madrugada - 1);
                fechaBase.setHours(18, 0, 0); // 6 PM
            }
        } else {
            fechaBase.setDate(fechaBase.getDate() + dias - madrugada);
            fechaBase.setHours(12, 0, 0); // Medio día
        }

        reservaResumenFechaSalida.value = fechaParseString(fechaBase);
        reservaResumenFechaSalidaValor.value = fechaJsToJavaParseString(fechaBase);
        reservaResumenMontoTotal.value = calcularMontoTotal(reservaResumenSelectServicio, reservaResumenDiasAlojamiento, 0, 0, reservaPrecioInput);
        reservaResumenMontoTotalValor.value = calcularMontoTotal(reservaResumenSelectServicio, reservaResumenDiasAlojamiento, 0, 0, reservaPrecioInput);

        let fHoy = new Date();
        let fSalida = new Date(fechaBase);
        let esMismoDia = (fHoy.getDate() === fSalida.getDate()) && (fHoy.getMonth() === fSalida.getMonth()) && (fHoy.getFullYear() === fSalida.getFullYear());
        let precio_estado = esMismoDia && (servicio === "MEDIO") ? (reserva_precio_habitacion / 2) : reserva_precio_habitacion;

        let descuento = parseFloat(reservaResumenDescuento.value) || 0;

        reservaResumenDescuento.value = (descuento).toFixed(2);
        reservaResumenMontoHabitacion.value = precio_estado.toFixed(2);

        reservaactualizarFilas();

        // let montoTotal = parseFloat(reservaResumenMontoTotal.value) || 0;
        // let adelanto = parseFloat(reservaResumenMontoAdelanto.value) || 0;
        // let descuento = parseFloat(reservaResumenDescuento.value) || 0;
        // reservaResumenDescuento.value = descuento == 0 ? 0 : descuento;
        // reservaResumenMontoAdelanto.value = adelanto == 0 ? 0 : adelanto;

        // if (montoTotal > (adelanto + descuento)) {
        //     // Hay un monto pendiente de cobro
        //     reservaResumenPorCobrar.value = (adelanto - montoTotal - descuento).toFixed(2);
        //     reservaResumenVuelto.value = 0.0;

        //     // Estilos
        //     reservaResumenPorCobrar.classList.add("bg-danger-subtle");
        //     reservaResumenPorCobrar.classList.remove("bg-success-subtle");

        //     reservaResumenVuelto.classList.remove("bg-success-subtle", "bg-danger-subtle");
        // } else {
        //     // Hay vuelto
        //     reservaResumenPorCobrar.value = 0.0;
        //     reservaResumenVuelto.value = (adelanto - montoTotal - descuento).toFixed(2);

        //     // Estilos
        //     reservaResumenPorCobrar.classList.remove("bg-danger-subtle");
        //     reservaResumenPorCobrar.classList.add("bg-success-subtle");

        //     reservaResumenVuelto.classList.add("bg-success-subtle");
        //     reservaResumenVuelto.classList.remove("bg-danger-subtle");
        // }


        // if (reservaResumenVuelto.value >= 0 && reservaResumenPorCobrar.value == 0) {
        //     reservaResumenSelectEstado.value = "PAGADO";
        //     // reservaResumenSelectEstado.setAttribute("disabled", true);
        //     reservaResumenSelectEstado.style.backgroundColor = "#d4edda"; // verde claro
        // } else {
        //     reservaResumenSelectEstado.value = "POR COBRAR";
        //     // reservaResumenSelectEstado.removeAttribute("disabled");
        //     reservaResumenSelectEstado.style.backgroundColor = "#f8d7da"; // rojo claro
        // }
    }

    // // Eventos
    // function reservaActualizarSelect() {
    //     let montoTotal = parseFloat(reservaResumenMontoTotal.value) || 0;
    //     let descuento = parseFloat(reservaResumenDescuento.value) || 0;

    //     if (this.value === "PAGADO") {
    //         reservaResumenMontoAdelanto.value = (montoTotal - descuento).toFixed(2);
    //         this.style.backgroundColor = "#d4edda"; // verde claro
    //     } else {
    //         reservaResumenMontoAdelanto.value = 0;
    //         this.style.backgroundColor = "#f8d7da"; // rojo claro
    //     }

    //     reservaactualizarResumen();
    // };

    if (reservaResumenFechaEntrada) {
        reservaResumenFechaEntrada.addEventListener("change", reservaactualizarResumen);
    }
    if (reservaResumenDiasAlojamiento) {
        reservaResumenDiasAlojamiento.addEventListener("input", reservamanejarBloqueoServicio);
    }
    if (reservaResumenSelectServicio) {
        reservaResumenSelectServicio.addEventListener("change", reservaactualizarResumen);
    }
    // if (reservaResumenDescuento) {
    //     reservaResumenDescuento.addEventListener("input", reservaactualizarResumen);
    // }
    // if (reservaResumenMontoTotalCobrado) {
    //     reservaResumenMontoTotalCobrado.addEventListener("input", reservaactualizarResumen);
    // }
    // if (reservaResumenSelectEstado) {
    //     reservaResumenSelectEstado.addEventListener("change", reservaActualizarSelect);
    // }




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
                        nombres.value = data.nombres || "";
                        apellidos.value = `${data.apellidoPaterno || ""
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
    function calcularFechaSalida(fecha, tipoServicio, p_fechaEntradaValor, esPrimerDia) {
        let fechaSalida = new Date(fecha);
        p_fechaEntradaValor = new Date(p_fechaEntradaValor);

        if (tipoServicio.includes("COMPLETO")) {
            if (p_fechaEntradaValor.getHours() >= 6) {
                fechaSalida.setDate(fechaSalida.getDate() + 1); // Día siguiente
            } else if (p_fechaEntradaValor.getHours() < 6 && !esPrimerDia) {
                fechaSalida.setDate(fechaSalida.getDate() + 1);
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
        if (event.target.id === "btnContinuarAlojamiento") {
            continuarAlojamiento();
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

    function continuarAlojamiento() {
        const confirmacion = confirm("¿Deseas extender 1 día más la estadía del cliente?");
        if (!confirmacion) return;

        const diasInput = document.getElementById("diasAlojamientoResumen");
        let diasActuales = parseInt(diasInput.value) || 1;
        diasInput.value = diasActuales + 1;

        actualizarResumen(); // Actualiza los cálculos

        // Cambiar botón de guardar/editar venta
        const btnEditarVenta = document.getElementById("btnGuardar"); // Asegúrate que este ID existe
        if (btnEditarVenta) {
            btnEditarVenta.classList.remove("btn-warning");
            btnEditarVenta.classList.add("btn-success");
            btnEditarVenta.innerText = "CONFIRMAR EXTENSIÓN DE ESTADÍA POR 1 DÍA";
        }

        // Mensaje visual temporal
        const mensajeExt = document.getElementById("mensajeExtension");
        if (mensajeExt) {
            confirm("Se ha extendido 1 día más la estadía del cliente. Revisa los siguientes cambios en el RESUMEN");
            mensajeExt.innerText = "✔️ ¡Se ha extendido 1 día más la estadía del cliente!, Revisa la informacion importante y confirma la extensión del servicio de alojamiento".toUpperCase();
            mensajeExt.classList.add("text-success", "fw-bold");
            mensajeExt.style.display = "block";

            // Ocultar después de 4 segundos
            setTimeout(() => {
                mensajeExt.style.display = "none";
            }, 20000);
        }
    }

});
