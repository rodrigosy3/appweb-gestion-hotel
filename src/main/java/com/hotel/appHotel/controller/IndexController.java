package com.hotel.appHotel.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
import com.hotel.appHotel.model.ClienteDTO;
import com.hotel.appHotel.model.Habitaciones;
import com.hotel.appHotel.model.HabitacionesEstado;
import com.hotel.appHotel.model.Usuarios;
import com.hotel.appHotel.model.Ventas;
import com.hotel.appHotel.model.VentasClientesHabitacion;
import com.hotel.appHotel.repository.VentasRepository;
import com.hotel.appHotel.service.HabitacionesEstadoService;
import com.hotel.appHotel.service.HabitacionesService;
import com.hotel.appHotel.service.RolesService;
import com.hotel.appHotel.service.UsuariosService;
import com.hotel.appHotel.service.VentasClientesHabitacionService;
import com.hotel.appHotel.service.VentasService;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class IndexController {
    private static final String VIEW_INICIO = "index";
    private static final String VIEW_EDITAR = "indexEditar";
    private static final String REDIRECT_INICIO = "redirect:/";
    boolean existeVenta = false;
    boolean existeVentaReservada = false;

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

    @GetMapping
    public String getDatos(@RequestParam(value = "fechaFiltro", required = false) String fechaFiltro, Model modelo) {
        List<Habitaciones> habitaciones = servicioHabitaciones.getHabitaciones();
        List<Ventas> ventas = servicioVentas.getVentas();
        List<Habitaciones> hab_antiguas = habitaciones.stream()
                .filter(habitacion -> !habitacion.getCategoria().equals("ANTIGUO")).collect(Collectors.toList());
        List<Habitaciones> hab_modernas = habitaciones.stream()
                .filter(habitacion -> !habitacion.getCategoria().equals("MODERNO")).collect(Collectors.toList());
        Map<Long, Ventas> ultimaVentaPorHabitacion = new HashMap<>();
        Map<Long, LocalDateTime> ultimaVentaPorHabitacionFechaFormateada = new HashMap<>();
        Map<Long, Ventas> ultimaVentaReservadaPorHabitacion = new HashMap<>();
        LocalDate fecha = LocalDate.now();

        for (Ventas venta : ventas) {
            LocalDate fechaEntrada = parseLocalDateTime(venta.getFecha_entrada()).toLocalDate();
            LocalDateTime fechaEntradaCompleto = parseLocalDateTime(venta.getFecha_entrada());
            Long id_habitacion = venta.getHabitacion().getId_habitacion();

            if (venta.getEstado_estadia().equals("SIN PROBLEMAS") && (fechaEntrada.equals(fecha))) {
                if (venta.getTipo_venta().equals("ALQUILER")) {
                    if (fechaEntrada.equals(fecha) && (!ultimaVentaPorHabitacion.containsKey(id_habitacion)
                            || parseLocalDateTime(venta.getFecha_entrada()).isAfter(
                                    parseLocalDateTime(
                                            ultimaVentaPorHabitacion.get(id_habitacion).getFecha_entrada())))) {
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
                            || parseLocalDateTime(venta.getFecha_entrada()).isBefore(
                                    parseLocalDateTime(
                                            ultimaVentaReservadaPorHabitacion.get(id_habitacion)
                                                    .getFecha_entrada()))) {
                        if (fechaEntrada.equals(fecha)) {
                            Habitaciones habTemporal = servicioHabitaciones.getHabitacionById(id_habitacion);

                            habTemporal.setEstado(servicioHabitacionesEstado.getByEstado("RESERVADA"));
                            servicioHabitaciones.updateHabitacion(habTemporal);
                        }

                        ultimaVentaReservadaPorHabitacion.put(id_habitacion, venta);
                    }
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
    public String editarIndexForm(@PathVariable Long id, Model modelo, RedirectAttributes redirectAttributes) {
        Habitaciones habitacion = servicioHabitaciones.getHabitacionById(id);
        List<Ventas> ventas = servicioVentas.getVentas(); // Obtener todas las ventas
        LocalDate fecha = LocalDate.now();

        // Filtrar las ventas por la habitación y que sean del día actual
        Optional<Ventas> ultimaVentaOpt = ventas.stream()
                .filter(venta -> venta.getHabitacion().getId_habitacion().equals(id) &&
                        parseLocalDateTime(venta.getFecha_entrada()).toLocalDate().equals(fecha)
                        && venta.getEstado_estadia().equals("SIN PROBLEMAS"))
                .max((v1, v2) -> parseLocalDateTime(v1.getFecha_entrada())
                        .compareTo(parseLocalDateTime(v2.getFecha_entrada())));

        // Si hay una venta activa hoy, usarla; si no, crear una nueva
        existeVenta = ultimaVentaOpt.isEmpty() ? false : true;
        Ventas ventaHabitacion = ultimaVentaOpt.orElse(new Ventas());

        List<Habitaciones> habitaciones = servicioHabitaciones.getHabitaciones();
        List<VentasClientesHabitacion> ventasClientesHabitacion = servicioVentasClientesHabitacion
                .getVentasClientesHabitacion();
        List<Habitaciones> hab_antiguas = habitaciones.stream()
                .filter(hab -> !hab.getCategoria().equals("ANTIGUO")).collect(Collectors.toList());
        List<Habitaciones> hab_modernas = habitaciones.stream()
                .filter(hab -> !hab.getCategoria().equals("MODERNO")).collect(Collectors.toList());
        Map<Long, Ventas> ultimaVentaPorHabitacion = new HashMap<>();
        Map<Long, Ventas> ultimaVentaReservadaPorHabitacion = new HashMap<>();
        Map<Long, LocalDateTime> ultimaVentaPorHabitacionFechaFormateada = new HashMap<>();

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

        modelo.addAttribute("ventasClientesHabitacion", ventasClientesHabitacion);
        modelo.addAttribute("habitacion", habitacion);
        modelo.addAttribute("ventaHabitacion", ventaHabitacion);

        // Filtrar las ventas por la habitación y que sean del día actual
        Optional<Ventas> ultimaVentaReservadaOpt = ventas.stream()
                .filter(venta -> venta.getHabitacion().getId_habitacion().equals(id) &&
                        parseLocalDateTime(venta.getFecha_entrada()).toLocalDate().equals(fecha)
                        && venta.getTipo_venta().equals("RESERVA") && !venta.getEstado_estadia().equals("RETIRADO"))
                .min((v1, v2) -> parseLocalDateTime(v1.getFecha_entrada())
                        .compareTo(parseLocalDateTime(v2.getFecha_entrada())));

        // Si hay una venta activa hoy, usarla; si no, crear una nueva
        existeVentaReservada = ultimaVentaReservadaOpt.isEmpty() ? false : true;
        Ventas ventaParaReserva = ultimaVentaReservadaOpt.orElse(new Ventas());
        modelo.addAttribute("ultimaVentaReservadaPorHabitacion", ultimaVentaReservadaPorHabitacion);
        modelo.addAttribute("ventaParaReserva", ventaParaReserva);

        modelo.addAttribute("habEstados", servicioHabitacionesEstado.getHabitacionesEstado());

        return VIEW_EDITAR;
    }

    @PostMapping("/{id}")
    public String actualizarVenta(@PathVariable Long id, @ModelAttribute("ventaHabitacion") Ventas ventaHabitacion,
            @RequestParam("clientesTemporales") String clientesJson,
            RedirectAttributes redirectAttributes) {
        Habitaciones habitacion = servicioHabitaciones.getHabitacionById(id);
        HabitacionesEstado estadoOcupado = servicioHabitacionesEstado.getByEstado("OCUPADO");

        Ventas ventaGuardada = new Ventas();
        ventaHabitacion.setHabitacion(habitacion);
        ventaHabitacion
                .setFecha_creacion(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        // Guardar la venta
        if (!existeVenta) {
            Usuarios usuario_admin = servicioUsuarios.getUsuarioById(2L) != null ? servicioUsuarios.getUsuarioById(2L)
                    : servicioUsuarios.getUsuarioById(1L);
            ventaHabitacion.setUsuario_responsable(usuario_admin);

            ventaGuardada = servicioVentas.createVenta(ventaHabitacion);
        } else {
            Ventas ventaExistente = servicioVentas.getVentaById(ventaHabitacion.getId_venta());
            Usuarios usuario_admin = servicioUsuarios.getUsuarioById(2L) != null ? servicioUsuarios.getUsuarioById(2L)
                    : servicioUsuarios.getUsuarioById(1L);

            // Copiar los datos de la venta a la venta existente
            BeanUtils.copyProperties(ventaHabitacion, ventaExistente, "usuario_responsable");
            ventaExistente.setUsuario_responsable(usuario_admin);
            servicioVentas.updateVenta(ventaExistente);

            ventaGuardada = servicioVentas.getVentaById(ventaExistente.getId_venta());
        }

        habitacion.setEstado(estadoOcupado);
        habitacion.setRazon_estado("");
        servicioHabitaciones.updateHabitacion(habitacion);

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

    @PostMapping("/reservar-habitacion/{id}")
    public String actualizarVentaReservada(@PathVariable Long id,
            @ModelAttribute("ventaParaReserva") Ventas ventaParaReserva,
            @RequestParam("reservaclientesTemporales") String clientesJson,
            RedirectAttributes redirectAttributes) {
        Habitaciones habitacion = servicioHabitaciones.getHabitacionById(id);
        HabitacionesEstado estadoReservada = servicioHabitacionesEstado.getByEstado("RESERVADA");
        HabitacionesEstado estadoDisponible = servicioHabitacionesEstado.getByEstado("DISPONIBLE");

        System.out.println("============================================");
        System.out.println("Tipode servicio: " + ventaParaReserva.getTipo_servicio());
        System.out.println("Estado: " + ventaParaReserva.getEstado());
        System.out.println("Estado de estadia: " + ventaParaReserva.getEstado_estadia());
        System.out.println("Descuento: " + ventaParaReserva.getDescuento());
        System.out.println("Monto adelanto: " + ventaParaReserva.getMonto_adelanto());
        System.out.println("Monto total: " + ventaParaReserva.getMonto_total());
        System.out.println("Tipo de venta: " + ventaParaReserva.getTipo_venta());
        System.out.println("Fecha entrada: " + ventaParaReserva.getFecha_entrada());
        System.out.println("Fecha salida: " + ventaParaReserva.getFecha_salida());
        System.out.println("Fecha creacion: " + ventaParaReserva.getFecha_creacion());
        System.out.println("Usuario responsable: " + ventaParaReserva.getUsuario_responsable());
        System.out.println("Tiempo estadia: " + ventaParaReserva.getTiempo_estadia());
        System.out.println("Estado estadia: " + ventaParaReserva.getEstado_estadia());
        System.out.println("Clientes: " + ventaParaReserva.getVentasClientesHabitacion().size());
        System.out.println("Clientes temporales: " + clientesJson);
        System.out.println("============================================");

        Ventas ventaReservada = new Ventas();
        ventaParaReserva.setHabitacion(habitacion);
        ventaParaReserva.setTipo_venta("RESERVA");
        ;
        ventaParaReserva
                .setFecha_creacion(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        // Guardar la venta
        if (!existeVentaReservada) {
            ventaParaReserva.setFecha_entrada(convertirFormatoFecha(ventaParaReserva.getFecha_entrada()));
            ventaReservada = servicioVentas.createVenta(ventaParaReserva);
        } else {
            Ventas ventaReservadaExistente = servicioVentas.getVentaById(ventaParaReserva.getId_venta());
            Usuarios usuario_admin = servicioUsuarios.getUsuarioById(2L) != null ? servicioUsuarios.getUsuarioById(2L)
                    : servicioUsuarios.getUsuarioById(1L);

            // Copiar los datos de la venta a la venta existente
            BeanUtils.copyProperties(ventaParaReserva, ventaReservadaExistente, "usuario_responsable");
            ventaReservadaExistente.setUsuario_responsable(usuario_admin);
            servicioVentas.updateVenta(ventaReservadaExistente);

            ventaReservada = servicioVentas.getVentaById(ventaReservadaExistente.getId_venta());
        }

        LocalDate fecha_entrada_reserva = parseLocalDateTime(ventaReservada.getFecha_entrada()).toLocalDate();
        System.out.println(fecha_entrada_reserva);
        System.out.println(LocalDate.now());
        if (fecha_entrada_reserva.isEqual(LocalDate.now())) {
            habitacion.setEstado(estadoReservada);
            habitacion.setRazon_estado("");

            servicioHabitaciones.updateHabitacion(habitacion);
        } else {
            habitacion.setEstado(estadoDisponible);
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
    public ResponseEntity<?> cancelarReserva(@PathVariable Long id) {
        Ventas venta = servicioVentas.getVentaById(id);
        Habitaciones habitacion = servicioHabitaciones.getHabitacionById(venta.getHabitacion().getId_habitacion());
        HabitacionesEstado estadoDisponible = servicioHabitacionesEstado.getByEstado("DISPONIBLE");

        servicioVentas.deleteVenta(id);

        habitacion.setEstado(estadoDisponible);
        servicioHabitaciones.updateHabitacion(habitacion);

        return ResponseEntity.ok().body(Collections.singletonMap("mensaje", "Reserva cancelada correctamente"));
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

}
