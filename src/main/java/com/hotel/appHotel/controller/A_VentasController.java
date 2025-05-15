package com.hotel.appHotel.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.hotel.appHotel.model.Habitaciones;
import com.hotel.appHotel.model.Usuarios;
import com.hotel.appHotel.model.Ventas;
import com.hotel.appHotel.repository.HabitacionesEstadoRepository;
import com.hotel.appHotel.service.HabitacionesService;
import com.hotel.appHotel.service.UsuariosService;
import com.hotel.appHotel.service.VentasService;

@Controller
@RequestMapping(value = "/admin/ventas")
public class A_VentasController {

    private static final String CARPETA_BASE = "templates_ventas/";
    private static final String VIEW_LISTAR = CARPETA_BASE + "ventas";
    private static final String VIEW_NUEVO = CARPETA_BASE + "form_nuevo_venta";
    private static final String VIEW_EDITAR = CARPETA_BASE + "form_editar_venta";
    private static final String REDIRECT_LISTAR = "redirect:/admin/ventas";

    @Autowired
    private VentasService servicio;

    @Autowired
    private UsuariosService usuariosServicio;

    @Autowired
    private HabitacionesService habitacionServicio;

    @Autowired
    private HabitacionesEstadoRepository habitacionesEstadoRepository;

    @GetMapping
    public String listarVentas(Model modelo) {
        modelo.addAttribute("ventas", obtenerVentas());
        modelo.addAttribute("fechasCreacion", obtenerFechasCreacionEnLocalDateTime());
        modelo.addAttribute("fechasEntrada", obtenerFechasEntradaEnLocalDateTime());
        modelo.addAttribute("fechasSalida", obtenerFechasSalidaEnLocalDateTime());

        return VIEW_LISTAR;
    }

    @GetMapping("/nuevo")
    public String nuevaVentaForm(Model modelo, RedirectAttributes redirectAttributes) {
        Ventas venta = new Ventas();
        List<Habitaciones> habitaciones = habitacionServicio.getHabitaciones()
                .stream()
                .filter(habitacion -> !habitacion.isEliminado())
                .sorted(Comparator.comparing(Habitaciones::getNumero))
                .collect(Collectors.toList());
        List<Usuarios> usuarios_responsables = usuariosServicio.getUsuarios()
                .stream()
                .filter(usuario -> usuario.getRol().getNivel() != 0 && !usuario.isEliminado())
                .collect(Collectors.toList());

        if (habitaciones.isEmpty()) {
            redirectAttributes.addFlashAttribute("error",
                    "No hay HABITACIONES necesarias para crear un nuevo registro aquí.");

            return REDIRECT_LISTAR;
        }

        if (usuarios_responsables.isEmpty()) {
            redirectAttributes.addFlashAttribute("error",
                    "No hay USUARIOS con el rol necesario para crear un nuevo registro aquí.");

            return REDIRECT_LISTAR;
        }

        modelo.addAttribute("habitaciones", habitaciones);
        modelo.addAttribute("usuarios", usuarios_responsables);
        modelo.addAttribute("ventas", obtenerVentas());
        modelo.addAttribute("fechasCreacion", obtenerFechasCreacionEnLocalDateTime());
        modelo.addAttribute("fechasEntrada", obtenerFechasEntradaEnLocalDateTime());
        modelo.addAttribute("fechasSalida", obtenerFechasSalidaEnLocalDateTime());
        modelo.addAttribute("venta", venta);

        return VIEW_NUEVO;
    }

    @PostMapping
    public String crearVenta(@ModelAttribute("venta") Ventas venta) {
        venta.setFecha_entrada(venta.getFecha_entrada());
        venta.setFecha_salida(venta.getFecha_salida());

        servicio.createVenta(venta);

        Habitaciones habitacion = habitacionServicio.getHabitacionById(venta.getHabitacion().getId_habitacion());

        habitacion.setEstado(habitacionesEstadoRepository.findByEstado("OCUPADO"));
        habitacionServicio.updateHabitacion(habitacion);

        return REDIRECT_LISTAR;
    }

    @GetMapping("/editar/{id}")
    public String editarVentaForm(@PathVariable Long id, Model modelo, RedirectAttributes redirectAttributes) {
        List<Habitaciones> habitaciones = habitacionServicio.getHabitaciones()
                .stream()
                .filter(habitacion -> !habitacion.isEliminado())
                .sorted(Comparator.comparing(Habitaciones::getNumero))
                .collect(Collectors.toList());
        List<Usuarios> usuarios_responsables = usuariosServicio.getUsuarios()
                .stream()
                .filter(usuario -> usuario.getRol().getNivel() != 0 && !usuario.isEliminado())
                .collect(Collectors.toList());

        if (habitaciones.isEmpty()) {
            redirectAttributes.addFlashAttribute("error",
                    "No hay HABITACIONES necesarias para crear un nuevo registro aquí.");

            return REDIRECT_LISTAR;
        }

        if (usuarios_responsables.isEmpty()) {
            redirectAttributes.addFlashAttribute("error",
                    "No hay USUARIOS con el rol necesario para crear un nuevo registro aquí.");

            return REDIRECT_LISTAR;
        }

        Ventas venta = servicio.getVentaById(id);
        venta.setFecha_entrada(venta.getFecha_entrada());
        venta.setFecha_salida(venta.getFecha_salida());

        modelo.addAttribute("habitaciones", habitaciones);
        modelo.addAttribute("usuarios", usuarios_responsables);
        modelo.addAttribute("ventas", obtenerVentas());
        modelo.addAttribute("fechasCreacion", obtenerFechasCreacionEnLocalDateTime());
        modelo.addAttribute("fechasEntrada", obtenerFechasEntradaEnLocalDateTime());
        modelo.addAttribute("fechasSalida", obtenerFechasSalidaEnLocalDateTime());
        modelo.addAttribute("venta", servicio.getVentaById(id));

        return VIEW_EDITAR;
    }

    @PostMapping("/{id}")
    public String actualizarVenta(@PathVariable Long id,
            @ModelAttribute("venta") Ventas venta, Model modelo) {
        Ventas ventaExistente = servicio.getVentaById(id);

        ventaExistente.setUsuario_responsable(venta.getUsuario_responsable());
        ventaExistente.setFecha_entrada(venta.getFecha_entrada());
        ventaExistente.setFecha_salida(venta.getFecha_salida());
        ventaExistente.setDescuento(venta.getDescuento());
        ventaExistente.setMonto_adelanto(venta.getMonto_adelanto());
        ventaExistente.setMonto_total(venta.getMonto_total());
        ventaExistente.setEstado(venta.getEstado());
        ventaExistente.setTipo_venta(venta.getTipo_venta());
        ventaExistente.setTiempo_estadia(venta.getTiempo_estadia());
        ventaExistente.setHabitacion(venta.getHabitacion());
        ventaExistente.setTipo_servicio(venta.getTipo_servicio());

        ventaExistente
                .setFecha_creacion(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")));

        servicio.updateVenta(ventaExistente);

        Habitaciones habitacion = habitacionServicio
                .getHabitacionById(ventaExistente.getHabitacion().getId_habitacion());

        if (ventaExistente.getTipo_venta().equals("ALQUILER")) {
            habitacion.setEstado(habitacionesEstadoRepository.findByEstado("OCUPADO"));
        } else if (ventaExistente.getEstado().equals("RESERVA")) {
            habitacion.setEstado(habitacionesEstadoRepository.findByEstado("RESERVADA"));
        } else {
            habitacion.setEstado(habitacionesEstadoRepository.findByEstado("DISPONIBLE"));
        }

        habitacionServicio.updateHabitacion(habitacion);

        return REDIRECT_LISTAR;
    }

    @PostMapping("/eliminar/{id}")
    public String eliminarVenta(@PathVariable Long id) {
        Ventas ventaExistente = servicio.getVentaById(id);

        ventaExistente.setEliminado(true);

        servicio.updateVenta(ventaExistente);

        return REDIRECT_LISTAR;
    }

    private List<Ventas> obtenerVentas() {
        return servicio.getVentas()
                .stream()
                .filter(venta -> !venta.isEliminado()) // Filtrar las que no estén eliminadas
                .sorted(Comparator.comparing(Ventas::getId_venta).reversed()) // Ordenar en orden descendente
                .collect(Collectors.toList());
    }

    private HashMap<Long, LocalDateTime> obtenerFechasCreacionEnLocalDateTime() {
        HashMap<Long, LocalDateTime> fechasCreacion = new HashMap<>();

        for (Ventas venta : obtenerVentas()) {
            fechasCreacion.put(venta.getId_venta(), LocalDateTime.parse(venta.getFecha_creacion()));
        }

        return fechasCreacion;
    }

    private HashMap<Long, LocalDateTime> obtenerFechasEntradaEnLocalDateTime() {
        HashMap<Long, LocalDateTime> fechasEntrada = new HashMap<>();

        for (Ventas venta : obtenerVentas()) {
            fechasEntrada.put(venta.getId_venta(), LocalDateTime.parse(venta.getFecha_entrada()));
        }

        return fechasEntrada;
    }

    private HashMap<Long, LocalDateTime> obtenerFechasSalidaEnLocalDateTime() {
        HashMap<Long, LocalDateTime> fechasSalida = new HashMap<>();

        for (Ventas venta : obtenerVentas()) {
            fechasSalida.put(venta.getId_venta(), LocalDateTime.parse(venta.getFecha_salida()));
        }

        return fechasSalida;
    }
}
