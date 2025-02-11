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
        LocalDate fecha = LocalDate.now();
        Map<Long, LocalDateTime> ultimaVentaPorHabitacionFechaFormateada = new HashMap<>();

        for (Ventas venta : ventas) {
            LocalDate fechaEntrada = parseLocalDateTime(venta.getFecha_entrada()).toLocalDate();
            LocalDateTime fechaEntradaCompleto = parseLocalDateTime(venta.getFecha_entrada());

            if (!venta.getEstado_estadia().equals("RETIRADO")) {
                if (fechaEntrada.equals(fecha)) {
                    Long id_habitacion = venta.getHabitacion().getId_habitacion();

                    if (!ultimaVentaPorHabitacion.containsKey(id_habitacion)
                            || parseLocalDateTime(venta.getFecha_entrada()).isAfter(
                                    parseLocalDateTime(
                                            ultimaVentaPorHabitacion.get(id_habitacion).getFecha_entrada()))) {
                        ultimaVentaPorHabitacion.put(id_habitacion, venta);
                    }
                }

                if (fechaEntradaCompleto.toLocalDate().equals(fecha) && !venta.getEstado().equals("RETIRADO")) {
                    Long id_habitacion = venta.getHabitacion().getId_habitacion();

                    if (!ultimaVentaPorHabitacionFechaFormateada.containsKey(id_habitacion)
                            || parseLocalDateTime(venta.getFecha_entrada()).isAfter(
                                    ultimaVentaPorHabitacionFechaFormateada.get(id_habitacion))) {

                        ultimaVentaPorHabitacionFechaFormateada.put(id_habitacion, fechaEntradaCompleto);
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
                        && !venta.getEstado_estadia().equals("RETIRADO"))
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
        Map<Long, LocalDateTime> ultimaVentaPorHabitacionFechaFormateada = new HashMap<>();

        for (Ventas venta : ventas) {
            LocalDate fechaEntrada = parseLocalDateTime(venta.getFecha_entrada()).toLocalDate();
            LocalDateTime fechaEntradaCompleto = parseLocalDateTime(venta.getFecha_entrada());

            if (fechaEntrada.equals(fecha) && !venta.getEstado_estadia().equals("RETIRADO")) {
                Long id_habitacion = venta.getHabitacion().getId_habitacion();

                if (!ultimaVentaPorHabitacion.containsKey(id_habitacion)
                        || parseLocalDateTime(venta.getFecha_entrada()).isAfter(
                                parseLocalDateTime(ultimaVentaPorHabitacion.get(id_habitacion).getFecha_entrada()))) {
                    ultimaVentaPorHabitacion.put(id_habitacion, venta);
                }
            }

            if (fechaEntradaCompleto.toLocalDate().equals(fecha) && !venta.getEstado_estadia().equals("RETIRADO")) {
                Long id_habitacion = venta.getHabitacion().getId_habitacion();

                if (!ultimaVentaPorHabitacionFechaFormateada.containsKey(id_habitacion)
                        || parseLocalDateTime(venta.getFecha_entrada()).isAfter(
                                ultimaVentaPorHabitacionFechaFormateada.get(id_habitacion))) {

                    ultimaVentaPorHabitacionFechaFormateada.put(id_habitacion, fechaEntradaCompleto);
                }
            }
        }

        modelo.addAttribute("hab_antiguas", hab_antiguas);
        modelo.addAttribute("hab_modernas", hab_modernas);
        modelo.addAttribute("ventasClientesHabitacion", ventasClientesHabitacion);
        modelo.addAttribute("ultimaVentaPorHabitacion", ultimaVentaPorHabitacion);
        modelo.addAttribute("ultimaVentaPorHabitacionFechaFormateada", ultimaVentaPorHabitacionFechaFormateada);
        modelo.addAttribute("habitacion", habitacion);
        modelo.addAttribute("ventaHabitacion", ventaHabitacion);

        return VIEW_EDITAR;
    }

    @PostMapping("/{id}")
    public String actualizarVenta(@PathVariable Long id, @ModelAttribute("ventaHabitacion") Ventas ventaHabitacion,
            @RequestParam("clientesTemporales") String clientesJson,
            RedirectAttributes redirectAttributes) {
        Habitaciones habitacion = servicioHabitaciones.getHabitacionById(id);
        HabitacionesEstado estadoOcupado = servicioHabitacionesEstado.getByEstado("OCUPADO");

        System.out.println("============================================");
        System.out.println("Tipode servicio: " + ventaHabitacion.getTipo_servicio());
        System.out.println("Estado: " + ventaHabitacion.getEstado());
        System.out.println("Estado de estadia: " + ventaHabitacion.getEstado_estadia());
        System.out.println("Descuento: " + ventaHabitacion.getDescuento());
        System.out.println("Monto adelanto: " + ventaHabitacion.getMonto_adelanto());
        System.out.println("Monto total: " + ventaHabitacion.getMonto_total());
        System.out.println("Tipo de venta: " + ventaHabitacion.getTipo_venta());
        System.out.println("Fecha entrada: " + ventaHabitacion.getFecha_entrada());
        System.out.println("Fecha salida: " + ventaHabitacion.getFecha_salida());
        System.out.println("Fecha creacion: " + ventaHabitacion.getFecha_creacion());
        System.out.println("Usuario responsable: " + ventaHabitacion.getUsuario_responsable());
        System.out.println("Tiempo estadia: " + ventaHabitacion.getTiempo_estadia());
        System.out.println("Estado estadia: " + ventaHabitacion.getEstado_estadia());
        System.out.println("Clientes: " + ventaHabitacion.getVentasClientesHabitacion().size());
        System.out.println("Clientes temporales: " + clientesJson);
        System.out.println("============================================");

        Ventas ventaGuardada = new Ventas();
        ventaHabitacion.setHabitacion(habitacion);
        ventaHabitacion
                .setFecha_creacion(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        // Guardar la venta
        if (!existeVenta) {
            ventaGuardada = servicioVentas.createVenta(ventaHabitacion);
        } else {
            Ventas ventaExistente = servicioVentas.getVentaById(ventaHabitacion.getId_venta());

            // Copiar los datos de la venta a la venta existente
            BeanUtils.copyProperties(ventaHabitacion, ventaExistente, "usuario_responsable");
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

    public LocalDateTime parseLocalDateTime(String fechaStr) {
        DateTimeFormatter formatterSinSegundos = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm a");

        return LocalDateTime.parse(fechaStr, formatterSinSegundos);
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

    @PostMapping("/actualizar-estado-habitacion")
    public String actualizarEstadoHabitacion(@RequestParam("id") Long id,
            @RequestParam("nuevoEstado") String nuevoEstado,
            @RequestParam("msgEstado") String msgEstado,
            RedirectAttributes redirectAttributes) {
        HabitacionesEstado estadoNuevo = servicioHabitacionesEstado.getByEstado(nuevoEstado);
        Habitaciones habitacion = servicioHabitaciones.getHabitacionById(id);

        habitacion.setEstado(estadoNuevo);
        habitacion.setRazon_estado(msgEstado);
        
        servicioHabitaciones.updateHabitacion(habitacion);

        return REDIRECT_INICIO;
    }
}
