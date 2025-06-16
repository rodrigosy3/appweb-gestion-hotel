package com.hotel.appHotel.controller;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hotel.appHotel.config.TicketPrinter;
import com.hotel.appHotel.model.Cajas;
import com.hotel.appHotel.model.ClienteDTO;
import com.hotel.appHotel.model.Credenciales;
import com.hotel.appHotel.model.Habitaciones;
import com.hotel.appHotel.model.HabitacionesContenido;
import com.hotel.appHotel.model.HabitacionesEstado;
import com.hotel.appHotel.model.Tickets;
import com.hotel.appHotel.model.Usuarios;
import com.hotel.appHotel.model.Ventas;
import com.hotel.appHotel.model.VentasClientesHabitacion;
import com.hotel.appHotel.repository.CredencialesRepository;
import com.hotel.appHotel.service.HabitacionesContenidoService;
import com.hotel.appHotel.service.HabitacionesEstadoService;
import com.hotel.appHotel.service.HabitacionesService;
import com.hotel.appHotel.service.RolesService;
import com.hotel.appHotel.service.TicketsService;
import com.hotel.appHotel.service.UsuariosService;
import com.hotel.appHotel.service.VentasClientesHabitacionService;
import com.hotel.appHotel.service.VentasService;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.hotel.appHotel.repository.VentasClientesHabitacionRepository;
import com.hotel.appHotel.service.CajasService;

@Controller
public class IndexController {

    private static final String VIEW_INICIO = "index";
    private static final String VIEW_EDITAR = "indexEditar";
    private static final String REDIRECT_INICIO = "redirect:/";
    // boolean existeVentaReservada = false;

    @Autowired
    HabitacionesService servicioHabitaciones;

    @Autowired
    UsuariosService servicioUsuarios;

    @Autowired
    VentasService servicioVentas;

    @Autowired
    CajasService servicioCajas;

    @Autowired
    VentasClientesHabitacionService servicioVentasClientesHabitacion;

    @Autowired
    VentasClientesHabitacionRepository repositorioVentasClientesHabitacion;

    @Autowired
    RolesService servicioRoles;

    @Autowired
    HabitacionesEstadoService servicioHabitacionesEstado;

    @Autowired
    HabitacionesContenidoService servicioHabitacionesContenido;

    @Autowired
    TicketsService servicioTickets;

    @Autowired
    CredencialesRepository repositorioCredenciales;

    @GetMapping
    public String getDatos(@RequestParam(value = "fechaFiltro", required = false) String fechaFiltro, Model modelo) {
        Map<Long, Ventas> ultimaVentaPorHabitacion = new HashMap<>();
        Map<Long, LocalDateTime> ultimaVentaPorHabitacionFechaEntrada = new HashMap<>();
        Map<Long, LocalDateTime> ultimaVentaPorHabitacionFechaSalida = new HashMap<>();

        Map<Long, Ventas> ventasVencidas = new HashMap<>();
        Map<Long, LocalDateTime> ventasVencidasFechaEntrada = new HashMap<>();
        Map<Long, LocalDateTime> ventasVencidasFechaSalida = new HashMap<>();

        Map<Long, Integer> mapAlertaSalida = new HashMap<>();

        LocalDateTime fechaBase = LocalDateTime.now();

        if (fechaFiltro != null && !fechaFiltro.isEmpty()) {
            fechaBase = LocalDateTime.parse(fechaFiltro + "T00:00:00");

            for (Ventas venta : obtenerVentas()) {
                LocalDateTime fechaEntrada = parseStringToLocalDateTime(venta.getFecha_entrada());
                LocalDateTime fechaSalida = parseStringToLocalDateTime(venta.getFecha_salida());

                Long id_habitacion = venta.getHabitacion().getId_habitacion();

                if (fechaEntrada.toLocalDate().equals(fechaBase.toLocalDate())
                        || fechaSalida.toLocalDate().equals(fechaBase.toLocalDate())
                        || (fechaEntrada.isBefore(fechaBase) && fechaSalida.isAfter(fechaBase))) {

                    if (ultimaVentaPorHabitacion.containsKey(id_habitacion)) {
                        if (fechaEntrada.isAfter(
                                parseStringToLocalDateTime(
                                        ultimaVentaPorHabitacion.get(id_habitacion).getFecha_entrada()))) {

                            ultimaVentaPorHabitacion.put(id_habitacion, venta);
                            ultimaVentaPorHabitacionFechaEntrada.put(id_habitacion, fechaEntrada);
                            ultimaVentaPorHabitacionFechaSalida.put(id_habitacion, fechaSalida);
                        }
                    } else {
                        ultimaVentaPorHabitacion.put(id_habitacion, venta);
                        ultimaVentaPorHabitacionFechaEntrada.put(id_habitacion, fechaEntrada);
                        ultimaVentaPorHabitacionFechaSalida.put(id_habitacion, fechaSalida);
                    }
                }
            }
        } else {
            for (Ventas venta : obtenerVentasActivas()) {
                LocalDateTime fechaEntrada = parseStringToLocalDateTime(venta.getFecha_entrada());
                LocalDateTime fechaSalida = parseStringToLocalDateTime(venta.getFecha_salida());

                Duration tiempoRestante = Duration.between(fechaBase, fechaSalida);

                Long id_habitacion = venta.getHabitacion().getId_habitacion();

                if (fechaEntrada.toLocalDate().equals(fechaBase.toLocalDate())
                        || fechaEntrada.isBefore(fechaBase)) {
                    if (fechaSalida.isAfter(fechaBase) || fechaSalida.equals(fechaBase)) {
                        if (ultimaVentaPorHabitacion.containsKey(id_habitacion)) {
                            if (fechaEntrada.isAfter(
                                    parseStringToLocalDateTime(
                                            ultimaVentaPorHabitacion.get(id_habitacion).getFecha_entrada()))) {

                                if (!tiempoRestante.isNegative()) {
                                    if (tiempoRestante.toMinutes() <= 60) {
                                        mapAlertaSalida.put(id_habitacion, 1);
                                    } else if (tiempoRestante.toMinutes() <= 360) {
                                        mapAlertaSalida.put(id_habitacion, 6);
                                    }
                                }

                                // MODIFICAR EL MONTO Y ESTADO CUANDO ES UN NUEVO DIA DE SERVICIO
                                if (fechaBase.isAfter(parseStringToLocalDateTime(venta.getUltimaFecha()))
                                        && venta.getEstado().equals("PAGADO")) {

                                }

                                ultimaVentaPorHabitacion.put(id_habitacion, venta);
                                ultimaVentaPorHabitacionFechaEntrada.put(id_habitacion, fechaEntrada);
                                ultimaVentaPorHabitacionFechaSalida.put(id_habitacion, fechaSalida);
                            }
                        } else {
                            if (!tiempoRestante.isNegative()) {
                                if (tiempoRestante.toMinutes() <= 60) {
                                    mapAlertaSalida.put(id_habitacion, 1);
                                } else if (tiempoRestante.toMinutes() <= 360) {
                                    mapAlertaSalida.put(id_habitacion, 6);
                                }
                            }

                            ultimaVentaPorHabitacion.put(id_habitacion, venta);
                            ultimaVentaPorHabitacionFechaEntrada.put(id_habitacion, fechaEntrada);
                            ultimaVentaPorHabitacionFechaSalida.put(id_habitacion, fechaSalida);
                        }
                    } else {
                        ventasVencidas.put(id_habitacion, venta);
                        ventasVencidasFechaEntrada.put(id_habitacion, fechaEntrada);
                        ventasVencidasFechaSalida.put(id_habitacion, fechaSalida);
                    }
                }
                // servicioVentas.actualizarCobroDiario(venta.getId_venta());
            }
        }

        double ventaTotalHbAntiguas = 0;
        double ventaTotalHbModernas = 0;
        List<Cajas> cajas = servicioCajas.getCajasDelDia();

        if (!cajas.isEmpty()) {
            ventaTotalHbAntiguas = cajas.stream()
                    .filter(caja -> caja.getVenta().getHabitacion().getCategoria().equals("ANTIGUO"))
                    .mapToDouble(Cajas::getMonto)
                    .sum();

            ventaTotalHbModernas = cajas.stream()
                    .filter(caja -> caja.getVenta().getHabitacion().getCategoria().equals("MODERNO"))
                    .mapToDouble(Cajas::getMonto)
                    .sum();
        }

        modelo.addAttribute("ventaTotalHbAntiguas", ventaTotalHbAntiguas);
        modelo.addAttribute("ventaTotalHbModernas", ventaTotalHbModernas);

        modelo.addAttribute("hab_antiguas", obtenerHabitacionesAntiguas());
        modelo.addAttribute("hab_modernas", obtenerHabitacionesModernas());

        modelo.addAttribute("ultimaVentaPorHabitacion", ultimaVentaPorHabitacion);
        modelo.addAttribute("ultimaVentaPorHabitacionFechaEntrada", ultimaVentaPorHabitacionFechaEntrada);
        modelo.addAttribute("ultimaVentaPorHabitacionFechaSalida", ultimaVentaPorHabitacionFechaSalida);
        modelo.addAttribute("mapAlertaSalida", mapAlertaSalida);

        modelo.addAttribute("ventasVencidas", ventasVencidas);
        modelo.addAttribute("ventasVencidasFechaEntrada", ventasVencidasFechaEntrada);
        modelo.addAttribute("ventasVencidasFechaSalida", ventasVencidasFechaSalida);

        modelo.addAttribute("fechaFiltro", fechaFiltro);

        return VIEW_INICIO;
    }

    @GetMapping("/editar/{idHabitacion}")
    public String editarIndexForm(@PathVariable Long idHabitacion,
            @RequestParam(value = "idVenta", required = false) Long idVenta,
            Model modelo, RedirectAttributes redirectAttributes) {

        Habitaciones habitacion = servicioHabitaciones.getHabitacionById(idHabitacion); // OBTENER HABITACION
        Ventas ventaHabitacion = idVenta == null ? new Ventas() : servicioVentas.getVentaById(idVenta); // OBTENER VENTA
        Ventas ventaParaReserva = new Ventas(); // OBTENER PRIMERA VENTA RESERVADA POR HABITACION

        if (idVenta != null && ventaHabitacion.getTipo_venta().equals("RESERVA")) {
            ventaParaReserva = ventaHabitacion;
            ventaHabitacion = new Ventas();
        }

        modelo.addAttribute("habitacion", habitacion);
        modelo.addAttribute("ventaHabitacion", ventaHabitacion);
        modelo.addAttribute("ventaParaReserva", ventaParaReserva);

        modelo.addAttribute("habEstados", servicioHabitacionesEstado.getHabitacionesEstado());

        return VIEW_EDITAR;

    }

    @PostMapping("/{idHabitacion}")
    public String actualizarVenta(@PathVariable Long idHabitacion,
            @ModelAttribute("ventaHabitacion") Ventas ventaHabitacion,
            @RequestParam(value = "clientesTemporales", required = false) String clientesJson,
            @RequestParam(value = "opcionImpresion", required = false) String opcionImpresion,
            RedirectAttributes redirectAttributes) {
        Ventas ventaGuardada;
        // Boolean huboCambios;
        // Logica para asignar el usuario responsable del cambio de la venta
        String dni = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<Credenciales> credencialOpt = repositorioCredenciales.buscarPorDni(dni);
        Usuarios usuario_responsable = credencialOpt.get().getUsuario();

        // Guardar la venta
        if (ventaHabitacion.getId_venta() == null) {
            Habitaciones habitacion = servicioHabitaciones.getHabitacionById(idHabitacion);

            habitacion.setEstado(servicioHabitacionesEstado.getByEstado("OCUPADO"));
            habitacion.setRazon_estado("");
            servicioHabitaciones.updateHabitacion(habitacion);

            ventaHabitacion.setHabitacion(habitacion);

            ventaHabitacion.setUsuario_responsable(usuario_responsable);
            /////////////////////////////////////////////////////////////////////

            // huboCambios = false;
            ventaGuardada = servicioVentas.createVenta(ventaHabitacion);
        } else {
            Ventas ventaExistente = servicioVentas.getVentaById(ventaHabitacion.getId_venta());

            // huboCambios =
            // !ventaExistente.getTiempo_estadia().equals(ventaHabitacion.getTiempo_estadia())
            // || !ventaExistente.getEstado().equals(ventaHabitacion.getEstado())
            // || !ventaExistente.getDescuento().equals(ventaHabitacion.getDescuento())
            // || !ventaExistente.getMonto_total().equals(ventaHabitacion.getMonto_total())
            // || !ventaExistente.getMontoDiario().equals(ventaHabitacion.getMontoDiario())
            // ||
            // !ventaExistente.getTipo_servicio().equals(ventaHabitacion.getTipo_servicio());

            // Copiar los datos de la venta a la venta existente
            ventaExistente.setFecha_entrada(ventaHabitacion.getFecha_entrada());
            ventaExistente.setFecha_salida(ventaHabitacion.getFecha_salida());
            ventaExistente.setTiempo_estadia(ventaHabitacion.getTiempo_estadia());
            ventaExistente.setEstado(ventaHabitacion.getEstado());
            ventaExistente.setEstado_estadia(ventaHabitacion.getEstado_estadia());
            ventaExistente.setDescuento(ventaHabitacion.getDescuento());
            ventaExistente.setMonto_total(ventaHabitacion.getMonto_total());
            ventaExistente.setMontoDiario(ventaHabitacion.getMontoDiario());
            ventaExistente.setTipo_servicio(ventaHabitacion.getTipo_servicio());
            ventaExistente.setTipo_venta(ventaHabitacion.getTipo_venta());
            ventaExistente.setTotalCobrado(ventaHabitacion.getTotalCobrado());
            ventaExistente.setUltimaFecha(ventaHabitacion.getUltimaFecha());

            servicioVentas.updateVenta(ventaExistente);

            ventaGuardada = servicioVentas.getVentaById(ventaExistente.getId_venta());
        }

        // LOGICQA PARA REGISTRAR EN CAJA
        List<Cajas> cajasExistentes = servicioCajas.getCajasPorVenta(ventaGuardada.getId_venta());
        double montoPorRegistrar = 0;

        if (!ventaGuardada.getUltimaFecha().equals("")) {
            // Tomamos solo la fecha (yyyy-MM-dd) de la última fecha de servicio registrada
            LocalDateTime ultimaFechaInicioVenta = LocalDateTime.parse(ventaGuardada.getUltimaFecha());
            LocalDateTime ultimaFechaFinVenta = LocalDateTime.parse(ventaGuardada.getUltimaFecha());
            LocalDateTime fechaSalidaVenta = LocalDateTime.parse(ventaGuardada.getFecha_salida());
            LocalDateTime hoy = LocalDateTime.now();

            if (ventaGuardada.getTipo_servicio().equals("MEDIO")
                    && fechaSalidaVenta.toLocalDate().equals(hoy.toLocalDate())
                    && hoy.isAfter(ultimaFechaInicioVenta)) {
                // ultimaFecha.plusDays(1).withHour(12).withMinute(0).withSecond(0);
                ultimaFechaFinVenta = ultimaFechaFinVenta.withHour(18).withMinute(0).withSecond(0);
            } else {
                ultimaFechaFinVenta = ultimaFechaFinVenta.plusDays(1).withHour(12).withMinute(0).withSecond(0);
            }

            double montoCajaExistente = 0.0;
            boolean existeCajaHoy = false;

            // boolean existeCajaHoy = cajasExistentes.stream()
            // .anyMatch(
            // caja -> {
            // LocalDateTime cajaFecha = LocalDateTime.parse(caja.getFechaRegistro());
            // montoCajaExistente = caja.getMonto();
            // System.out.println("ultimaFechaInicioVenta: " + ultimaFechaInicioVenta + "
            // cajaFecha"
            // + cajaFecha + " ultimaFechaFinVenta" + auxUltimaFechaFinVenta);

            // return cajaFecha.isAfter(ultimaFechaInicioVenta)
            // && cajaFecha.isBefore(auxUltimaFechaFinVenta);
            // });

            for (Cajas caja : cajasExistentes) {
                LocalDateTime cajaFecha = LocalDateTime.parse(caja.getFechaRegistro());

                // System.out.println("ultimaFechaInicioVenta: " + ultimaFechaInicioVenta +
                //         "   cajaFecha: " + cajaFecha +
                //         "   ultimaFechaFinVenta: " + ultimaFechaFinVenta);

                if (cajaFecha.isAfter(ultimaFechaInicioVenta) && cajaFecha.isBefore(ultimaFechaFinVenta)) {
                    montoCajaExistente = caja.getMonto();
                    existeCajaHoy = true;
                    break;
                }
            }

            // System.out.println(existeCajaHoy);

            if ("PAGADO".equals(ventaGuardada.getEstado())) {
                if (!existeCajaHoy)
                    montoPorRegistrar = ventaGuardada.getMontoDiario();
                else if (existeCajaHoy && (montoCajaExistente < 0))
                    montoPorRegistrar = ventaGuardada.getMontoDiario();
            } else if ("POR COBRAR".equals(ventaGuardada.getEstado()) && existeCajaHoy) {
                montoPorRegistrar = ventaGuardada.getMontoDiario() * (-1);
            }

            if (montoPorRegistrar != 0) {
                Cajas caja = new Cajas();

                caja.setUsuarioResponsable(usuario_responsable);
                caja.setMonto(montoPorRegistrar);
                caja.setFechaRegistro(LocalDateTime.now().toString());
                caja.setVenta(ventaGuardada);

                servicioCajas.createCaja(caja);
            }
        }

        // double montoPorRegistrar = 0;
        // List<Cajas> cajasExistentes =
        // servicioCajas.getCajasPorVenta(ventaGuardada.getId_venta());

        // if (huboCambios) {
        // // Sumamos todo lo que ya se guardó en Cajas para esta venta
        // // double totalRegistrado = cajasExistentes.stream()
        // // .mapToDouble(Cajas::getMonto)
        // // .sum();

        // // Calculamos “nuevoTotal” que representa la cantidad que debería estar en
        // caja
        // // según el estado actual de la venta:
        // double montoDiarioPagado;

        // if ("PAGADO".equals(ventaGuardada.getEstado())) {
        // // Si está en PAGADO, consideramos el total menos descuento
        // montoDiarioPagado = ventaGuardada.getMontoDiario();
        // } else {
        // // Si no está aún PAGADO (POR COBRAR), solo tomamos el adelanto menos
        // descuento
        // montoDiarioPagado = ventaGuardada.getMonto_adelanto() -
        // ventaGuardada.getDescuento();
        // }

        // // La diferencia entre lo que “debería” y lo que YA existe en Cajas
        // montoPorRegistrar = montoDiarioPagado;
        // } else if (!huboCambios && cajasExistentes.isEmpty()) {
        // if ("PAGADO".equals(ventaGuardada.getEstado())) {
        // montoPorRegistrar = ventaGuardada.getMontoDiario();
        // } else {
        // montoPorRegistrar = ventaGuardada.getMontoDiario();
        // }
        // }

        // if (montoPorRegistrar != 0) {
        // Cajas caja = new Cajas();

        // caja.setMonto(montoPorRegistrar);
        // caja.setFechaRegistro(LocalDateTime.now().toString());
        // caja.setVenta(ventaGuardada);

        // servicioCajas.createCaja(caja);
        // }

        // FUNCIONES PARA CLIENTES POR HABITACIÓN
        // Convertir el JSON recibido en una lista de objetos Cliente
        ObjectMapper objectMapper = new ObjectMapper();
        List<ClienteDTO> clientes = new ArrayList<>();

        try {
            clientes = objectMapper.readValue(clientesJson, new TypeReference<List<ClienteDTO>>() {
            });
        } catch (JsonProcessingException e) {
            System.out.println("ERROR: " + e.getMessage());
        }

        // Procesar clientes
        for (ClienteDTO cliente : clientes) {
            Usuarios usuario = servicioUsuarios.getUsuarioByDni(cliente.getDni());

            if (usuario == null) {
                usuario = new Usuarios();

                usuario.setDni(cliente.getDni());
                usuario.setNombres(cliente.getNombres());
                usuario.setApellidos(cliente.getApellidos());
                usuario.setEdad(cliente.getEdad());
                usuario.setCelular(cliente.getCelular());
                usuario.setRol(servicioRoles.getRolById(1L));

                usuario = servicioUsuarios.createUsuario(usuario);
            }

            VentasClientesHabitacion ventaClienteHabitacionExistente = servicioVentasClientesHabitacion
                    .getByVentaCliente(ventaGuardada.getId_venta(), usuario.getId_usuario());

            if (!cliente.isEliminado() && ventaClienteHabitacionExistente == null) {
                ventaClienteHabitacionExistente = new VentasClientesHabitacion();

                ventaClienteHabitacionExistente.setUsuario_alojado(usuario);
                ventaClienteHabitacionExistente.setVenta(ventaGuardada);

                servicioVentasClientesHabitacion.createVentaClienteHabitacion(ventaClienteHabitacionExistente);
            } else if (cliente.isEliminado() && ventaClienteHabitacionExistente != null) {
                servicioVentasClientesHabitacion.deleteByVentaCliente(ventaGuardada.getId_venta(),
                        usuario.getId_usuario());
            }
        }

        redirectAttributes.addFlashAttribute("mensaje", "Venta guardada correctamente");
        List<VentasClientesHabitacion> listaClientes = repositorioVentasClientesHabitacion
                .findByVentaId(ventaGuardada.getId_venta());

        if ("SI_IMPRIMIR".equals(opcionImpresion)) {
            Tickets ticket = servicioTickets.createTicket(ventaGuardada);
            TicketPrinter.imprimirTicketVentaRealizada(ventaGuardada, ticket, usuario_responsable,
                    listaClientes, true);
        } else if ("NO_IMPRIMIR".equals(opcionImpresion)) {
            redirectAttributes.addFlashAttribute("mensaje", "Venta guardada correctamente");
        }

        return REDIRECT_INICIO;
    }

    @PostMapping("/reservar-habitacion/{id}")
    public String actualizarVentaReservada(@PathVariable Long id,
            @ModelAttribute("ventaParaReserva") Ventas ventaParaReserva,
            @RequestParam("reservaclientesTemporales") String clientesJson,
            @RequestParam(value = "opcionImpresion", required = false) String opcionImpresion,
            RedirectAttributes redirectAttributes) {
        Habitaciones habitacion = servicioHabitaciones.getHabitacionById(id);

        Ventas ventaReservada;
        // Boolean huboCambios;

        // Logica para asignar el usuario responsable del cambio de la venta
        String dni = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<Credenciales> credencialOpt = repositorioCredenciales.buscarPorDni(dni);
        Usuarios usuario_responsable = credencialOpt.get().getUsuario();
        /////////////////////////////////////////////////////////////////////

        // Guardar la venta
        if (ventaParaReserva.getId_venta() == null) {
            ventaParaReserva.setHabitacion(habitacion);
            ventaParaReserva.setTipo_venta(ventaParaReserva.getTipo_venta());
            ventaParaReserva.setFecha_entrada(ventaParaReserva.getFecha_entrada());

            ventaParaReserva.setUsuario_responsable(usuario_responsable);

            // huboCambios = false;
            ventaReservada = servicioVentas.createVenta(ventaParaReserva);
        } else {
            Ventas ventaReservadaExistente = servicioVentas.getVentaById(ventaParaReserva.getId_venta());

            // huboCambios = !ventaReservadaExistente.getTiempo_estadia()
            // .equals(ventaParaReserva.getTiempo_estadia())
            // ||
            // !ventaReservadaExistente.getDescuento().equals(ventaParaReserva.getDescuento())
            // ||
            // !ventaReservadaExistente.getMonto_total().equals(ventaParaReserva.getMonto_total())
            // ||
            // !ventaReservadaExistente.getTipo_servicio().equals(ventaParaReserva.getTipo_servicio());

            // Copiar los datos de la venta a la venta existente
            ventaReservadaExistente.setFecha_entrada(ventaParaReserva.getFecha_entrada());
            ventaReservadaExistente.setFecha_salida(ventaParaReserva.getFecha_salida());
            ventaReservadaExistente.setTiempo_estadia(ventaParaReserva.getTiempo_estadia());
            ventaReservadaExistente.setEstado(ventaParaReserva.getEstado());
            ventaReservadaExistente.setEstado_estadia(ventaParaReserva.getEstado_estadia());
            ventaReservadaExistente.setDescuento(ventaParaReserva.getDescuento());
            ventaReservadaExistente.setMonto_total(ventaParaReserva.getMonto_total());
            ventaReservadaExistente.setMonto_adelanto(ventaParaReserva.getMonto_adelanto());
            ventaReservadaExistente.setTipo_servicio(ventaParaReserva.getTipo_servicio());
            ventaReservadaExistente.setTipo_venta(ventaParaReserva.getTipo_venta());
            ventaReservadaExistente.setUsuario_responsable(usuario_responsable);
            ventaReservadaExistente.setEstado("POR COBRAR");
            // BeanUtils.copyProperties(ventaParaReserva, ventaReservadaExistente,
            // "habitacion", "usuario_responsable",
            // "fecha_creacion");
            servicioVentas.updateVenta(ventaReservadaExistente);

            ventaReservada = servicioVentas.getVentaById(ventaReservadaExistente.getId_venta());
        }

        LocalDate fecha_entrada_reserva = parseStringToLocalDateTime(ventaReservada.getFecha_entrada()).toLocalDate();

        if (fecha_entrada_reserva.isEqual(LocalDate.now())) {
            habitacion.setEstado(servicioHabitacionesEstado.getByEstado("RESERVADA"));
            habitacion.setRazon_estado("Habitación reservada");

            servicioHabitaciones.updateHabitacion(habitacion);
        } else {
            habitacion.setEstado(servicioHabitacionesEstado.getByEstado("DISPONIBLE"));
            habitacion.setRazon_estado("Habitación libre para alquilar");

            servicioHabitaciones.updateHabitacion(habitacion);
        }

        // double montoPorRegistrar = 0;
        // List<Cajas> cajasExistentes =
        // servicioCajas.getCajasPorVenta(ventaReservada.getId_venta());

        // if (huboCambios) {
        // // Sumamos todo lo que ya se guardó en Cajas para esta venta
        // double totalRegistrado = cajasExistentes.stream()
        // .mapToDouble(Cajas::getMonto)
        // .sum();

        // // Calculamos “nuevoTotal” que representa la cantidad que debería estar en
        // caja
        // // según el estado actual de la venta:
        // double nuevoTotal;

        // if ("PAGADO".equals(ventaReservada.getEstado())) {
        // // Si está en PAGADO, consideramos el total menos descuento
        // nuevoTotal = ventaReservada.getMonto_total() - ventaReservada.getDescuento();
        // } else {
        // // Si no está aún PAGADO (POR COBRAR), solo tomamos el adelanto menos
        // descuento
        // nuevoTotal = ventaReservada.getMonto_adelanto() -
        // ventaReservada.getDescuento();
        // }

        // // La diferencia entre lo que “debería” y lo que YA existe en Cajas
        // montoPorRegistrar = nuevoTotal - totalRegistrado;
        // } else if (!huboCambios && cajasExistentes.isEmpty()) {
        // if ("PAGADO".equals(ventaReservada.getEstado())) {
        // montoPorRegistrar = ventaReservada.getMonto_total() -
        // ventaReservada.getDescuento();
        // } else {
        // montoPorRegistrar = ventaReservada.getMonto_adelanto() -
        // ventaReservada.getDescuento();
        // }
        // }

        // if (montoPorRegistrar != 0) {
        // Cajas caja = new Cajas();

        // caja.setMonto(montoPorRegistrar);
        // caja.setFechaRegistro(LocalDateTime.now().toString());
        // caja.setVenta(ventaReservada);

        // servicioCajas.createCaja(caja);
        // }

        // FUNCIONES PARA CLIENTES POR HABITACIÓN
        // Convertir el JSON recibido en una lista de objetos Cliente
        ObjectMapper objectMapper = new ObjectMapper();
        List<ClienteDTO> clientes = new ArrayList<>();

        try {
            clientes = objectMapper.readValue(clientesJson, new TypeReference<List<ClienteDTO>>() {
            });
        } catch (JsonProcessingException e) {
            System.out.println("ERROR: " + e.getMessage());
        }

        // Procesar clientes
        for (ClienteDTO cliente : clientes) {
            Usuarios usuario = servicioUsuarios.getUsuarioByDni(cliente.getDni());

            if (usuario == null) {
                usuario = new Usuarios();

                usuario.setDni(cliente.getDni());
                usuario.setNombres(cliente.getNombres());
                usuario.setApellidos(cliente.getApellidos());
                usuario.setEdad(cliente.getEdad());
                usuario.setCelular(cliente.getCelular());
                usuario.setRol(servicioRoles.getRolById(1L));

                usuario = servicioUsuarios.createUsuario(usuario);
            }

            VentasClientesHabitacion ventaClienteHabitacionExistente = servicioVentasClientesHabitacion
                    .getByVentaCliente(ventaReservada.getId_venta(), usuario.getId_usuario());

            if (!cliente.isEliminado() && ventaClienteHabitacionExistente == null) {
                ventaClienteHabitacionExistente = new VentasClientesHabitacion();

                ventaClienteHabitacionExistente.setUsuario_alojado(usuario);
                ventaClienteHabitacionExistente.setVenta(ventaReservada);

                servicioVentasClientesHabitacion.createVentaClienteHabitacion(ventaClienteHabitacionExistente);
            } else if (cliente.isEliminado() && ventaClienteHabitacionExistente != null) {
                servicioVentasClientesHabitacion.deleteByVentaCliente(ventaReservada.getId_venta(),
                        usuario.getId_usuario());
            }
        }

        redirectAttributes.addFlashAttribute("mensaje", "Venta guardada correctamente");
        List<VentasClientesHabitacion> listaClientes = repositorioVentasClientesHabitacion
                .findByVentaId(ventaReservada.getId_venta());

        if ("SI_IMPRIMIR".equals(opcionImpresion)) {
            Tickets ticket = servicioTickets.createTicket(ventaReservada);
            TicketPrinter.imprimirTicketVentaRealizada(ventaReservada, ticket, usuario_responsable, listaClientes,
                    true);
        } else if ("NO_IMPRIMIR".equals(opcionImpresion)) {
            redirectAttributes.addFlashAttribute("mensaje", "Venta guardada correctamente");
        }

        return REDIRECT_INICIO;
    }

    @GetMapping("/buscar-cliente")
    @ResponseBody
    public Map<String, Object> buscarCliente(@RequestParam("dni") String dni) {
        Map<String, Object> respuesta = new HashMap<>();

        Usuarios cliente = servicioUsuarios.getUsuarioByDni(dni);
        if (cliente != null) {
            respuesta.put("encontrado", true);
            respuesta.put("nombres", cliente.getNombres());
            respuesta.put("apellidos", cliente.getApellidos());
            respuesta.put("edad", cliente.getEdad());
            respuesta.put("celular", cliente.getCelular());
            respuesta.put("estado_vetado", cliente.getEstado_vetado());
            respuesta.put("razon_vetado", cliente.getRazon_vetado());
        } else {
            respuesta.put("encontrado", false);
        }

        return respuesta;
    }

    @PostMapping("/actualizar-estado/{id}")
    @ResponseBody
    public ResponseEntity<?> actualizarEstadoVenta(@PathVariable Long id) {
        Ventas venta = servicioVentas.getVentaById(id);
        Habitaciones habitacion = servicioHabitaciones.getHabitacionById(venta.getHabitacion().getId_habitacion());
        HabitacionesEstado estadoLimpieza = servicioHabitacionesEstado.getByEstado("LIMPIEZA");

        venta.setEstado_estadia("RETIRADO");
        servicioVentas.updateVenta(venta);

        habitacion.setEstado(estadoLimpieza);
        servicioHabitaciones.updateHabitacion(habitacion);

        return ResponseEntity.ok().body(Collections.singletonMap("mensaje", "Estado actualizado correctamente"));
    }

    @PostMapping("/cancelar-reserva/{id}")
    @ResponseBody
    public ResponseEntity<String> cancelarReserva(@PathVariable Long id) {
        Ventas venta = servicioVentas.getVentaById(id);
        Habitaciones habitacion = servicioHabitaciones.getHabitacionById(venta.getHabitacion().getId_habitacion());
        HabitacionesEstado estadoDisponible = servicioHabitacionesEstado.getByEstado("DISPONIBLE");

        // Logica para asignar el usuario responsable del cambio de la venta
        // Usuarios usuario_admin = servicio.getUsuarioById(2L) != null ?
        // servicio.getUsuarioById(2L)
        // : servicio.getUsuarioById(1L);
        String dni = SecurityContextHolder.getContext().getAuthentication().getName();

        Optional<Credenciales> credencialOpt = repositorioCredenciales.buscarPorDni(dni);
        Usuarios usuario_responsable = credencialOpt.get().getUsuario();
        /////////////////////////////////////////////////////////////////////

        venta.setUsuario_responsable(usuario_responsable);
        venta.setTipo_venta("RESERVA CANCELADA");
        servicioVentas.updateVenta(venta);

        habitacion.setEstado(estadoDisponible);
        servicioHabitaciones.updateHabitacion(habitacion);

        return ResponseEntity.ok("Reserva cancelada correctamente");
    }

    @PostMapping("/habilitar-reserva/{id}")
    public String actualizarVentaReservada(@PathVariable Long id) {
        Ventas venta = servicioVentas.getVentaById(id);

        // Logica para asignar el usuario responsable del cambio de la venta
        // Usuarios usuario_admin = servicio.getUsuarioById(2L) != null ?
        // servicio.getUsuarioById(2L)
        // : servicio.getUsuarioById(1L);
        String dni = SecurityContextHolder.getContext().getAuthentication().getName();

        Optional<Credenciales> credencialOpt = repositorioCredenciales.buscarPorDni(dni);
        Usuarios usuario_responsable = credencialOpt.get().getUsuario();
        /////////////////////////////////////////////////////////////////////

        venta.setUsuario_responsable(usuario_responsable);
        venta.setTipo_venta("ALQUILER");
        servicioVentas.updateVenta(venta);

        Habitaciones habitacionExistente = servicioHabitaciones
                .getHabitacionById(venta.getHabitacion().getId_habitacion());
        HabitacionesEstado habEstado = servicioHabitacionesEstado.getByEstado("OCUPADO");

        habitacionExistente.setEstado(habEstado);
        servicioHabitaciones.updateHabitacion(habitacionExistente);

        return REDIRECT_INICIO;
    }

    @PostMapping("/actualizar-estado-habitacion")
    public String actualizarEstadoHabitacion(@RequestParam("id") Long id,
            @RequestParam("nuevoEstado") String nuevoEstado,
            @RequestParam("msgEstado") String msgEstado,
            @RequestParam(value = "id_ventaHabitacion", required = false) Long id_ventaHabitacion,
            RedirectAttributes redirectAttributes) {
        HabitacionesEstado estadoNuevo = servicioHabitacionesEstado.getByEstado(nuevoEstado);
        Habitaciones habitacion = servicioHabitaciones.getHabitacionById(id);

        if (id_ventaHabitacion != null) {
            Ventas venta = servicioVentas.getVentaById(id_ventaHabitacion);

            venta.setEstado_estadia("SIN PROBLEMAS");
            servicioVentas.updateVenta(venta);
        }

        habitacion.setEstado(estadoNuevo);
        habitacion.setRazon_estado(msgEstado);

        servicioHabitaciones.updateHabitacion(habitacion);

        return REDIRECT_INICIO;
    }

    @PostMapping("/editar-estado/habitacion/{id}")
    public String actualizarEstadoHabitacion(@PathVariable Long id,
            @ModelAttribute("habitacion") Habitaciones habitacion) {
        Habitaciones habitacionExistente = servicioHabitaciones.getHabitacionById(id);
        habitacionExistente.setEstado(habitacion.getEstado());
        habitacionExistente.setRazon_estado(habitacion.getRazon_estado());

        servicioHabitaciones.updateHabitacion(habitacionExistente);

        return REDIRECT_INICIO;
    }

    @PostMapping("/habitaciones/contenido/actualizar/{id}")
    public ResponseEntity<String> actualizarEstado(@PathVariable Long id, @RequestBody HabitacionesContenido datos) {
        HabitacionesContenido contenido = servicioHabitacionesContenido.getHabitacionContenidoById(id);

        if (contenido == null) {
            return ResponseEntity.badRequest().body("Error: No se encontró la característica.");
        }

        contenido.setEstado_caracteristica(datos.getEstado_caracteristica()); // Actualiza el estado
        servicioHabitacionesContenido.updateHabitacionContenido(contenido); // Guarda en la BD

        return ResponseEntity.ok("Estado actualizado correctamente.");
    }

    private List<Habitaciones> obtenerHabitaciones() {
        return servicioHabitaciones.getHabitaciones()
                .stream()
                .filter(habitacion -> !habitacion.isEliminado())
                .sorted(Comparator.comparing(Habitaciones::getNumero))
                .collect(Collectors.toList());
    }

    private List<Habitaciones> obtenerHabitacionesAntiguas() {
        return obtenerHabitaciones()
                .stream()
                .filter(habitacion -> habitacion.getCategoria().equals("ANTIGUO"))
                .collect(Collectors.toList());
    }

    private List<Habitaciones> obtenerHabitacionesModernas() {
        return obtenerHabitaciones()
                .stream()
                .filter(habitacion -> habitacion.getCategoria().equals("MODERNO"))
                .collect(Collectors.toList());
    }

    private List<Ventas> obtenerVentas() {
        return servicioVentas.getVentasNoEliminadas();
        // return servicioVentas.getVentas()
        // .stream()
        // .filter(ventas -> !ventas.isEliminado())
        // .collect(Collectors.toList());
    }

    private List<Ventas> obtenerVentasActivas() {
        return servicioVentas.getVentasActivas();
        // return obtenerVentas()
        // .stream()
        // .filter(venta -> (venta.getEstado_estadia().equals("SIN PROBLEMAS")
        // && !venta.getTipo_venta().equals("RESERVA CANCELADA")))
        // .collect(Collectors.toList());
    }

    public LocalDateTime parseStringToLocalDateTime(String fechaStr) {
        return LocalDateTime.parse(fechaStr);
    }

}
