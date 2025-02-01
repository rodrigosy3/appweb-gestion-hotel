package com.hotel.appHotel.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.hotel.appHotel.model.Habitaciones;
import com.hotel.appHotel.model.HabitacionesEstado;
import com.hotel.appHotel.model.HabitacionesPrecio;
import com.hotel.appHotel.model.Usuarios;
import com.hotel.appHotel.model.Ventas;
import com.hotel.appHotel.repository.HabitacionesEstadoRepository;
import com.hotel.appHotel.repository.HabitacionesRepository;
import com.hotel.appHotel.service.UsuariosService;
import com.hotel.appHotel.service.VentasService;
import com.hotel.appHotel.service.HabitacionesEstadoService;
import com.hotel.appHotel.service.HabitacionesPrecioService;
import com.hotel.appHotel.service.HabitacionesService;

import org.springframework.web.bind.annotation.PostMapping;

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
    private HabitacionesPrecioService habitacionesPrecioServicio;

    @Autowired
    private UsuariosService usuariosServicio;

    @Autowired
    private HabitacionesService habitacionServicio;

    @Autowired
    private HabitacionesEstadoRepository habitacionesEstadoRepository;

    @GetMapping
    public String listarVentas(Model modelo) {
        modelo.addAttribute("ventas", servicio.getVentas());

        return VIEW_LISTAR;
    }

    @GetMapping("/nuevo")
    public String nuevaVentaForm(Model modelo, RedirectAttributes redirectAttributes) {
        Ventas venta = new Ventas();
        List<HabitacionesPrecio> habitacionesPrecio = habitacionesPrecioServicio.getHabitacionesPrecio();
        List<Habitaciones> habitaciones = habitacionServicio.getHabitaciones();
        List<Usuarios> usuarios = usuariosServicio.getUsuarios();
        List<Usuarios> usuarios_responsables = new ArrayList<>();

        for (Usuarios usuario : usuarios) {
            if (usuario.getRol().getNivel() != 0) {
                usuarios_responsables.add(usuario);
            }
        }

        if (habitacionesPrecio.isEmpty()) {
            redirectAttributes.addFlashAttribute("error",
                    "No hay PRECIOS DE HABITACIONES necesarias para crear un nuevo registro aquí.");

            return REDIRECT_LISTAR;
        }

        if (usuarios_responsables.isEmpty()) {
            redirectAttributes.addFlashAttribute("error",
                    "No hay USUARIOS con el rol necesario para crear un nuevo registro aquí.");

            return REDIRECT_LISTAR;
        }

        modelo.addAttribute("habitacionesPrecio", habitacionesPrecio);
        modelo.addAttribute("habitaciones", habitaciones);
        modelo.addAttribute("usuarios", usuarios_responsables);
        modelo.addAttribute("ventas", servicio.getVentas());
        modelo.addAttribute("venta", venta);

        return VIEW_NUEVO;
    }

    @PostMapping
    public String crearVenta(@ModelAttribute("Venta") Ventas venta) {
        venta.setFecha_entrada(convertirFormatoFecha(venta.getFecha_entrada()));
        venta.setFecha_salida(convertirFormatoFecha(venta.getFecha_salida()));

        servicio.createVenta(venta);
        List<Habitaciones> habitaciones = habitacionServicio.getHabitaciones();

        for (Habitaciones habitacion : habitaciones) {
            if (habitacion.getId_habitacion().equals(venta.getHabitacion_precio().getHabitacion().getId_habitacion())) {
                habitacion.setEstado(habitacionesEstadoRepository.findByEstado("OCUPADO"));
                habitacionServicio.updateHabitacion(habitacion);

                return REDIRECT_LISTAR;
            }
        }

        return REDIRECT_LISTAR;
    }

    @GetMapping("/editar/{id}")
    public String editarVentaForm(@PathVariable Long id, Model modelo, RedirectAttributes redirectAttributes) {
        List<HabitacionesPrecio> habitacionesPrecio = habitacionesPrecioServicio.getHabitacionesPrecio();
        List<Habitaciones> habitaciones = habitacionServicio.getHabitaciones();
        List<Usuarios> usuarios = usuariosServicio.getUsuarios();
        List<Usuarios> usuarios_responsables = new ArrayList<>();

        for (Usuarios usuario : usuarios) {
            if (usuario.getRol().getNivel() != 0) {
                usuarios_responsables.add(usuario);
            }
        }

        if (habitacionesPrecio.isEmpty()) {
            redirectAttributes.addFlashAttribute("error",
                    "No hay PRECIOS DE HABITACIONES necesarias para crear un nuevo registro aquí.");

            return REDIRECT_LISTAR;
        }

        if (usuarios.isEmpty()) {
            redirectAttributes.addFlashAttribute("error",
                    "No hay USUARIOS con el rol necesario para crear un nuevo registro aquí.");

            return REDIRECT_LISTAR;
        }

        modelo.addAttribute("habitaciones", habitaciones);
        modelo.addAttribute("habitacionesPrecio", habitacionesPrecio);
        modelo.addAttribute("usuarios", usuarios_responsables);
        modelo.addAttribute("ventas", servicio.getVentas());
        modelo.addAttribute("venta", servicio.getVentaById(id));

        return VIEW_EDITAR;
    }

    @PostMapping("/{id}")
    public String actualizarVenta(@PathVariable Long id,
            @ModelAttribute("venta") Ventas venta, Model modelo) {
        Ventas ventaExistente = servicio.getVentaById(id);

        ventaExistente.setUsuario_responsable(venta.getUsuario_responsable());
        ventaExistente.setHabitacion_precio(venta.getHabitacion_precio());
        ventaExistente.setFecha_entrada(convertirFormatoFecha(venta.getFecha_entrada()));
        ventaExistente.setFecha_salida(convertirFormatoFecha(venta.getFecha_salida()));
        ventaExistente.setDescuento(venta.getDescuento());
        ventaExistente.setMonto_adelanto(venta.getMonto_adelanto());
        ventaExistente.setMonto_total(venta.getMonto_total());
        ventaExistente.setEstado(venta.getEstado());
        ventaExistente.setTipo_venta(venta.getTipo_venta());

        ventaExistente
                .setFecha_creacion(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        servicio.updateVenta(ventaExistente);

        List<Habitaciones> habitaciones = habitacionServicio.getHabitaciones();

        if (!ventaExistente.getTipo_venta().equals("COTIZACIÓN")) {
            for (Habitaciones habitacion : habitaciones) {
                if (habitacion.getId_habitacion()
                        .equals(venta.getHabitacion_precio().getHabitacion().getId_habitacion())
                        && habitacion.getEstado().getEstado().equals("LIBRE")
                        && ventaExistente.getEstado().equals("POR COBRAR")) {

                    habitacion.setEstado(habitacionesEstadoRepository.findByEstado("OCUPADO"));
                    habitacionServicio.updateHabitacion(habitacion);

                    return REDIRECT_LISTAR;
                } else if (habitacion.getEstado().getEstado().equals("OCUPADO")
                        && ventaExistente.getEstado().equals("CANCELADO")) {

                    habitacion.setEstado(habitacionesEstadoRepository.findByEstado("LIBRE"));
                    habitacionServicio.updateHabitacion(habitacion);

                    return REDIRECT_LISTAR;
                }

            }
        }

        return REDIRECT_LISTAR;
    }

    @GetMapping("/{id}")
    public String eliminarVenta(@PathVariable Long id) {
        servicio.deleteVenta(id);

        return REDIRECT_LISTAR;
    }

    private String convertirFormatoFecha(String fecha) {
        if (fecha == null || fecha.isEmpty()) {
            return fecha;
        }
        DateTimeFormatter inputFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME; // Formato recibido
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"); // Formato deseado
        return LocalDateTime.parse(fecha, inputFormatter).format(outputFormatter);
    }
}
