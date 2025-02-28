package com.hotel.appHotel.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
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
import com.hotel.appHotel.model.ClienteDTO;
import com.hotel.appHotel.model.Habitaciones;
import com.hotel.appHotel.model.HabitacionesContenido;
import com.hotel.appHotel.model.HabitacionesEstado;
import com.hotel.appHotel.model.Usuarios;
import com.hotel.appHotel.model.Ventas;
import com.hotel.appHotel.model.VentasClientesHabitacion;
import com.hotel.appHotel.repository.VentasRepository;
import com.hotel.appHotel.service.HabitacionesContenidoService;
import com.hotel.appHotel.service.HabitacionesEstadoService;
import com.hotel.appHotel.service.HabitacionesService;
import com.hotel.appHotel.service.RolesService;
import com.hotel.appHotel.service.UsuariosService;
import com.hotel.appHotel.service.VentasClientesHabitacionService;
import com.hotel.appHotel.service.VentasService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

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
    VentasRepository repositorioVentas;

    @Autowired
    VentasClientesHabitacionService servicioVentasClientesHabitacion;

    @Autowired
    RolesService servicioRoles;

    @Autowired
    HabitacionesEstadoService servicioHabitacionesEstado;

    @Autowired
    HabitacionesContenidoService servicioHabitacionesContenido;

    @GetMapping
    public String getDatos(@RequestParam(value = "fechaFiltro", required = false) String fechaFiltro, Model modelo) {
        List<Habitaciones> habitaciones = servicioHabitaciones.getHabitaciones();
        habitaciones = habitaciones.stream().sorted(Comparator.comparing(Habitaciones::getNumero))
                .collect(Collectors.toList());
        List<Ventas> ventas = servicioVentas.getVentas();
        List<Habitaciones> hab_antiguas = habitaciones.stream()
                .filter(habitacion -> !habitacion.getCategoria().equals("ANTIGUO")).collect(Collectors.toList());
        List<Habitaciones> hab_modernas = habitaciones.stream()
                .filter(habitacion -> !habitacion.getCategoria().equals("MODERNO")).collect(Collectors.toList());
        Map<Long, Ventas> ultimaVentaPorHabitacion = new HashMap<>();
        Map<Long, LocalDateTime> ultimaVentaPorHabitacionFechaFormateada = new HashMap<>();
        LocalDate fecha = LocalDate.now();

        for (Ventas venta : ventas) {
            LocalDate fechaEntrada = parseLocalDateTime(venta.getFecha_entrada()).toLocalDate();
            LocalDate fechaSalida = parseLocalDateTime(venta.getFecha_salida()).toLocalDate();
            LocalDateTime fechaEntradaCompleto = parseLocalDateTime(venta.getFecha_entrada());
            Long id_habitacion = venta.getHabitacion().getId_habitacion();

            if (venta.getEstado_estadia().equals("SIN PROBLEMAS") && !venta.getTipo_venta().equals("RESERVA CANCELADA")
                    && (fechaEntrada.equals(fecha) || (fechaEntrada.isBefore(fecha) && fechaSalida.isAfter(fecha)))) {
                if (!ultimaVentaPorHabitacion.containsKey(id_habitacion)
                        || parseLocalDateTime(venta.getFecha_entrada()).isAfter(
                                parseLocalDateTime(
                                        ultimaVentaPorHabitacion.get(id_habitacion).getFecha_entrada()))) {
                    ultimaVentaPorHabitacion.put(id_habitacion, venta);
                }

                if (!ultimaVentaPorHabitacionFechaFormateada.containsKey(id_habitacion)
                        || parseLocalDateTime(venta.getFecha_entrada()).isAfter(
                                ultimaVentaPorHabitacionFechaFormateada.get(id_habitacion))) {

                    ultimaVentaPorHabitacionFechaFormateada.put(id_habitacion, fechaEntradaCompleto);
                }
            }
        }

        modelo.addAttribute("hab_antiguas", hab_antiguas);
        modelo.addAttribute("hab_modernas", hab_modernas);
        modelo.addAttribute("ultimaVentaPorHabitacion", ultimaVentaPorHabitacion);
        modelo.addAttribute("ultimaVentaPorHabitacionFechaFormateada", ultimaVentaPorHabitacionFechaFormateada);

        return VIEW_INICIO;
    }

    @GetMapping("/editar/{id}")
    public String editarIndexForm(@PathVariable Long id,
            @RequestParam(value = "idVenta", required = false) Long idVenta,
            Model modelo, RedirectAttributes redirectAttributes) {
        List<Habitaciones> habitaciones = servicioHabitaciones.getHabitaciones();
        habitaciones = habitaciones.stream().sorted(Comparator.comparing(Habitaciones::getNumero))
                .collect(Collectors.toList());
        List<Ventas> ventas = servicioVentas.getVentas(); // Obtener todas las ventas
        List<Habitaciones> hab_antiguas = habitaciones.stream()
                .filter(hab -> !hab.getCategoria().equals("ANTIGUO")).collect(Collectors.toList());
        List<Habitaciones> hab_modernas = habitaciones.stream()
                .filter(hab -> !hab.getCategoria().equals("MODERNO")).collect(Collectors.toList());
        Map<Long, Ventas> ultimaVentaPorHabitacion = new HashMap<>();
        Map<Long, LocalDateTime> ultimaVentaPorHabitacionFechaFormateada = new HashMap<>();
        Map<Long, Ventas> ultimaVentaReservadaPorHabitacion = new HashMap<>();
        LocalDate fecha = LocalDate.now();

        for (Ventas venta : ventas) {
            LocalDate fechaEntrada = parseLocalDateTime(venta.getFecha_entrada()).toLocalDate();
            LocalDate fechaSalida = parseLocalDateTime(venta.getFecha_salida()).toLocalDate();
            LocalDateTime fechaEntradaCompleto = parseLocalDateTime(venta.getFecha_entrada());
            Long id_habitacion = venta.getHabitacion().getId_habitacion();

            if (venta.getEstado_estadia().equals("SIN PROBLEMAS")
                    && (fechaEntrada.equals(fecha) || (fechaEntrada.isBefore(fecha) && fechaSalida.isAfter(fecha)))) {
                if (venta.getTipo_venta().equals("ALQUILER")) {
                    if (!ultimaVentaPorHabitacion.containsKey(id_habitacion)
                            || parseLocalDateTime(venta.getFecha_entrada()).isAfter(
                                    parseLocalDateTime(
                                            ultimaVentaPorHabitacion.get(id_habitacion).getFecha_entrada()))) {
                        ultimaVentaPorHabitacion.put(id_habitacion, venta);
                    }

                    if (!ultimaVentaPorHabitacionFechaFormateada.containsKey(id_habitacion)
                            || parseLocalDateTime(venta.getFecha_entrada()).isAfter(
                                    ultimaVentaPorHabitacionFechaFormateada.get(id_habitacion))) {

                        ultimaVentaPorHabitacionFechaFormateada.put(id_habitacion, fechaEntradaCompleto);
                    }
                }

                if (venta.getTipo_venta().equals("RESERVA")) {
                    if (!ultimaVentaReservadaPorHabitacion.containsKey(id_habitacion)
                            || parseLocalDateTime(venta.getFecha_entrada()).isAfter(
                                    parseLocalDateTime(
                                            ultimaVentaReservadaPorHabitacion.get(id_habitacion)
                                                    .getFecha_entrada()))) {
                        ultimaVentaReservadaPorHabitacion.put(id_habitacion, venta);
                    }
                }
            }
        }

        modelo.addAttribute("hab_antiguas", hab_antiguas);
        modelo.addAttribute("hab_modernas", hab_modernas);
        modelo.addAttribute("ultimaVentaPorHabitacion", ultimaVentaPorHabitacion);
        modelo.addAttribute("ultimaVentaPorHabitacionFechaFormateada", ultimaVentaPorHabitacionFechaFormateada);
        //
        //
        //
        Habitaciones habitacion = servicioHabitaciones.getHabitacionById(id);
        // OBTENER VENTA
        Ventas ventaHabitacion = idVenta == null ? new Ventas() : servicioVentas.getVentaById(idVenta);
        Ventas ventaParaReserva = new Ventas();

        if (ventaHabitacion.getId_venta() != null && ventaHabitacion.getTipo_venta().equals("RESERVA")) {
            ventaParaReserva = ventaHabitacion;
            ventaHabitacion = new Ventas();
        }

        modelo.addAttribute("habitacion", habitacion);
        modelo.addAttribute("ventaHabitacion", ventaHabitacion);
        //
        //
        // Filtrar las ventas por la habitación y que sean del día actual
        // Optional<Ventas> ultimaVentaReservadaOpt = ventas.stream()
        //         .filter(venta -> venta.getHabitacion().getId_habitacion().equals(id) &&
        //                 parseLocalDateTime(venta.getFecha_entrada()).toLocalDate().equals(fecha)
        //                 && venta.getTipo_venta().equals("RESERVA"))
        //         .min((v1, v2) -> parseLocalDateTime(v1.getFecha_entrada())
        //                 .compareTo(parseLocalDateTime(v2.getFecha_entrada())));

        // Si hay una venta activa hoy, usarla; si no, crear una nueva
        // existeVentaReservada = ultimaVentaReservadaOpt.isEmpty() ? false : true;
        // Ventas ventaParaReserva = ultimaVentaReservadaOpt.orElse(new Ventas());
        modelo.addAttribute("ultimaVentaReservadaPorHabitacion", ultimaVentaReservadaPorHabitacion);
        modelo.addAttribute("ventaParaReserva", ventaParaReserva);

        modelo.addAttribute("habEstados", servicioHabitacionesEstado.getHabitacionesEstado());

        return VIEW_EDITAR;
    }

    @PostMapping("/{id}")
    public String actualizarVenta(@PathVariable Long id, @ModelAttribute("ventaHabitacion") Ventas ventaHabitacion,
            @RequestParam("clientesTemporales") String clientesJson,
            RedirectAttributes redirectAttributes) {
        Ventas ventaGuardada = new Ventas();

        // Guardar la venta
        if (ventaHabitacion.getId_venta() == null) {
            Usuarios usuario_admin = servicioUsuarios.getUsuarioById(2L) != null ? servicioUsuarios.getUsuarioById(2L)
                    : servicioUsuarios.getUsuarioById(1L);
            Habitaciones habitacion = servicioHabitaciones.getHabitacionById(id);

            habitacion.setEstado(servicioHabitacionesEstado.getByEstado("OCUPADO"));
            habitacion.setRazon_estado("");
            servicioHabitaciones.updateHabitacion(habitacion);

            ventaHabitacion.setHabitacion(habitacion);
            ventaHabitacion.setUsuario_responsable(usuario_admin);

            ventaGuardada = servicioVentas.createVenta(ventaHabitacion);
        } else {
            Ventas ventaExistente = servicioVentas.getVentaById(ventaHabitacion.getId_venta());

            // Copiar los datos de la venta a la venta existente
            BeanUtils.copyProperties(ventaHabitacion, ventaExistente, "habitacion", "usuario_responsable",
                    "fecha_creacion");
            servicioVentas.updateVenta(ventaExistente);

            ventaGuardada = servicioVentas.getVentaById(ventaExistente.getId_venta());
        }

        // FUNCIONES PARA CLIENTES POR HABITACIÓN
        // Convertir el JSON recibido en una lista de objetos Cliente
        ObjectMapper objectMapper = new ObjectMapper();
        List<ClienteDTO> clientes = new ArrayList<>();

        try {
            clientes = objectMapper.readValue(clientesJson, new TypeReference<List<ClienteDTO>>() {
            });
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        // Procesar clientes
        for (ClienteDTO cliente : clientes) {
            Usuarios usuario = servicioUsuarios.getUsuarioByDni(cliente.getDni());

            if (usuario == null) {
                usuario = new Usuarios();

                usuario.setDni(cliente.getDni());
                usuario.setNombres(cliente.getNombres());
                usuario.setApellidos(cliente.getApellidos());
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

        return REDIRECT_INICIO;
    }

    @Transactional
    @PostMapping("/reservar-habitacion/{id}")
    public String actualizarVentaReservada(@PathVariable Long id,
            @ModelAttribute("ventaParaReserva") Ventas ventaParaReserva,
            @RequestParam("reservaclientesTemporales") String clientesJson,
            RedirectAttributes redirectAttributes) {
        Habitaciones habitacion = servicioHabitaciones.getHabitacionById(id);
        HabitacionesEstado estadoReservada = servicioHabitacionesEstado.getByEstado("RESERVADA");

        Ventas ventaReservada = new Ventas();

        // Guardar la venta
        if (ventaParaReserva.getId_venta() == null) {
            Usuarios usuario_admin = servicioUsuarios.getUsuarioById(2L) != null ? servicioUsuarios.getUsuarioById(2L)
                    : servicioUsuarios.getUsuarioById(1L);

            ventaParaReserva.setHabitacion(habitacion);
            ventaParaReserva.setTipo_venta("RESERVA");
            ventaParaReserva.setFecha_entrada(convertirFormatoFecha(ventaParaReserva.getFecha_entrada()));
            ventaParaReserva.setUsuario_responsable(usuario_admin);

            ventaReservada = servicioVentas.createVenta(ventaParaReserva);
        } else {
            Ventas ventaReservadaExistente = servicioVentas.getVentaById(ventaParaReserva.getId_venta());

            // Copiar los datos de la venta a la venta existente
            BeanUtils.copyProperties(ventaParaReserva, ventaReservadaExistente, "usuario_responsable", "habitacion",
                    "fecha_creacion", "tipo_venta", "fecha_entrada");
            ventaReservadaExistente.setFecha_entrada(convertirFormatoFecha(ventaParaReserva.getFecha_entrada()));
            ventaReservadaExistente.setTipo_venta("RESERVA");
            servicioVentas.updateVenta(ventaReservadaExistente);

            ventaReservada = servicioVentas.getVentaById(ventaReservadaExistente.getId_venta());
        }

        LocalDate fecha_entrada_reserva = parseLocalDateTime(ventaReservada.getFecha_entrada()).toLocalDate();

        if (fecha_entrada_reserva.isEqual(LocalDate.now())) {
            habitacion.setEstado(estadoReservada);
            habitacion.setRazon_estado("");

            servicioHabitaciones.updateHabitacion(habitacion);
        }

        // FUNCIONES PARA CLIENTES POR HABITACIÓN
        // Convertir el JSON recibido en una lista de objetos Cliente
        ObjectMapper objectMapper = new ObjectMapper();
        List<ClienteDTO> clientes = new ArrayList<>();

        try {
            clientes = objectMapper.readValue(clientesJson, new TypeReference<List<ClienteDTO>>() {
            });
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        // Procesar clientes
        for (ClienteDTO cliente : clientes) {
            Usuarios usuario = servicioUsuarios.getUsuarioByDni(cliente.getDni());

            if (usuario == null) {
                usuario = new Usuarios();

                usuario.setDni(cliente.getDni());
                usuario.setNombres(cliente.getNombres());
                usuario.setApellidos(cliente.getApellidos());
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

        return REDIRECT_INICIO;
    }

    public LocalDateTime parseLocalDateTime(String fechaStr) {
        DateTimeFormatter formatterSinSegundos = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm a");

        return LocalDateTime.parse(fechaStr, formatterSinSegundos);
    }

    private String convertirFormatoFecha(String fecha) {
        DateTimeFormatter inputFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME; // Formato recibido
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm a"); // Formato deseado

        return LocalDateTime.parse(fecha, inputFormatter).format(outputFormatter);
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
            respuesta.put("estado_vetado", cliente.getEstado_vetado());
            respuesta.put("razon_vetado", cliente.getRazon_vetado());
        } else {
            respuesta.put("encontrado", false);
        }

        return respuesta;
    }

    @Transactional
    @PostMapping("/actualizar-estado/{id}")
    @ResponseBody
    public ResponseEntity<?> actualizarEstadoVenta(@PathVariable Long id) {
        Ventas venta = servicioVentas.getVentaById(id);
        Habitaciones habitacion = servicioHabitaciones.getHabitacionById(venta.getHabitacion().getId_habitacion());
        HabitacionesEstado estadoLimpieza = servicioHabitacionesEstado.getByEstado("LIMPIEZA");
        System.out.println("Añaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa " + id);
        venta.setEstado_estadia("RETIRADO");
        servicioVentas.updateVenta(venta);

        habitacion.setEstado(estadoLimpieza);
        servicioHabitaciones.updateHabitacion(habitacion);

        return ResponseEntity.ok().body(Collections.singletonMap("mensaje", "Estado actualizado correctamente"));
    }

    @Transactional
    @PostMapping("/cancelar-reserva/{id}")
    @ResponseBody
    public ResponseEntity<String> cancelarReserva(@PathVariable Long id) {
        Ventas venta = servicioVentas.getVentaById(id);
        Habitaciones habitacion = servicioHabitaciones.getHabitacionById(venta.getHabitacion().getId_habitacion());
        HabitacionesEstado estadoDisponible = servicioHabitacionesEstado.getByEstado("DISPONIBLE");

        venta.setTipo_venta("RESERVA CANCELADA");
        servicioVentas.updateVenta(venta);

        habitacion.setEstado(estadoDisponible);
        servicioHabitaciones.updateHabitacion(habitacion);

        return ResponseEntity.ok("Reserva cancelada correctamente");
    }

    @PostMapping("/habilitar-reserva/{id}")
    public String actualizarVentaReservada(@PathVariable Long id) {
        Ventas venta = servicioVentas.getVentaById(id);

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
            @RequestParam(value = "id_ventaHabitacion", required = false) String id_ventaHabitacion,
            RedirectAttributes redirectAttributes) {
        HabitacionesEstado estadoNuevo = servicioHabitacionesEstado.getByEstado(nuevoEstado);
        Habitaciones habitacion = servicioHabitaciones.getHabitacionById(id);

        if (id_ventaHabitacion != null) {
            Ventas venta = servicioVentas.getVentaById(Long.parseLong(id_ventaHabitacion));

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
}
