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
import com.hotel.appHotel.model.HabitacionesEstado;
import com.hotel.appHotel.model.HabitacionesTipos;
import com.hotel.appHotel.service.HabitacionesEstadoService;
import com.hotel.appHotel.service.HabitacionesService;
import com.hotel.appHotel.service.HabitacionesTiposService;

@Controller
@RequestMapping(value = "/admin/habitaciones")
public class A_HabitacionesController {

    private static final String CARPETA_BASE = "templates_habitaciones/";
    private static final String VIEW_LISTAR = CARPETA_BASE + "habitaciones";
    private static final String VIEW_NUEVO = CARPETA_BASE + "form_nuevo_habitacion";
    private static final String VIEW_EDITAR = CARPETA_BASE + "form_editar_habitacion";
    private static final String REDIRECT_LISTAR = "redirect:/admin/habitaciones";

    @Autowired
    private HabitacionesService servicio;

    @Autowired
    private HabitacionesTiposService habitacionesTiposServicio;

    @Autowired
    private HabitacionesEstadoService habitacionesEstadoServicio;

    @GetMapping
    public String listarHabitaciones(Model modelo) {
        modelo.addAttribute("habitaciones", obtenerHabitaciones());
        modelo.addAttribute("fechasCreacion", obtenerFechasCreacionEnLocalDateTime());

        return VIEW_LISTAR;
    }

    @GetMapping("/nuevo")
    public String nuevaHabitacionForm(Model modelo, RedirectAttributes redirectAttributes) {
        Habitaciones habitacion = new Habitaciones();
        List<HabitacionesTipos> habitacionesTipos = habitacionesTiposServicio.getHabitacionesTipos();
        List<HabitacionesEstado> habitacionesEstado = habitacionesEstadoServicio.getHabitacionesEstado();

        if (habitacionesTipos.isEmpty()) {
            redirectAttributes.addFlashAttribute("error",
                    "No hay registros en TIPOS DE HABITACIÓN para crear una habitación");

            return REDIRECT_LISTAR;
        } else if (habitacionesEstado.isEmpty()) {
            redirectAttributes.addFlashAttribute("error",
                    "No hay registros en ESTADOS DE HABITACIÓN para crear una habitación");

            return REDIRECT_LISTAR;
        } else {
            modelo.addAttribute("habitacionesTipos", habitacionesTipos);
            modelo.addAttribute("habitacionesEstado", habitacionesEstado);
            modelo.addAttribute("habitaciones", obtenerHabitaciones());
            modelo.addAttribute("fechasCreacion", obtenerFechasCreacionEnLocalDateTime());
            modelo.addAttribute("habitacion", habitacion);

            return VIEW_NUEVO;
        }
    }

    @PostMapping
    public String crearHabitacion(@ModelAttribute("habitacion") Habitaciones habitacion) {
        habitacion.setCategoria(habitacion.getCategoria().toUpperCase());

        servicio.createHabitacion(habitacion);

        return REDIRECT_LISTAR;
    }

    @GetMapping("/editar/{id}")
    public String editarHabitacionForm(@PathVariable Long id, Model modelo, RedirectAttributes redirectAttributes) {
        List<HabitacionesTipos> habitacionesTipos = habitacionesTiposServicio.getHabitacionesTipos();
        List<HabitacionesEstado> habitacionesEstado = habitacionesEstadoServicio.getHabitacionesEstado();

        if (habitacionesTipos.isEmpty()) {
            redirectAttributes.addFlashAttribute("error",
                    "No hay registros en TIPOS DE HABITACIÓN para crear una habitación");

            return REDIRECT_LISTAR;
        } else if (habitacionesEstado.isEmpty()) {
            redirectAttributes.addFlashAttribute("error",
                    "No hay registros en ESTADOS DE HABITACIÓN para crear una habitación");

            return REDIRECT_LISTAR;
        } else {
            modelo.addAttribute("habitacionesTipos", habitacionesTipos);
            modelo.addAttribute("habitacionesEstado", habitacionesEstado);
            modelo.addAttribute("habitaciones", obtenerHabitaciones());
            modelo.addAttribute("fechasCreacion", obtenerFechasCreacionEnLocalDateTime());
            modelo.addAttribute("habitacion", servicio.getHabitacionById(id));

            return VIEW_EDITAR;
        }
    }

    @PostMapping("/{id}")
    public String actualizarHabitacion(@PathVariable Long id, @ModelAttribute("habitacion") Habitaciones habitacion,
            Model modelo) {
        Habitaciones habitacionExistente = servicio.getHabitacionById(id);

        habitacionExistente.setNumero(habitacion.getNumero());
        habitacionExistente.setCategoria(habitacion.getCategoria().toUpperCase());
        habitacionExistente.setCapacidad(habitacion.getCapacidad());
        habitacionExistente.setPrecio(habitacion.getPrecio());
        habitacionExistente.setTipo(habitacion.getTipo());
        habitacionExistente.setEstado(habitacion.getEstado());

        habitacionExistente
                .setFecha_creacion(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")));

        servicio.updateHabitacion(habitacionExistente);

        return REDIRECT_LISTAR;
    }

    @PostMapping("/eliminar/{id}")
    public String eliminarHabitacion(@PathVariable Long id) {
        Habitaciones habitacionExistente = servicio.getHabitacionById(id);

        habitacionExistente.setEliminado(true);

        servicio.updateHabitacion(habitacionExistente);

        return REDIRECT_LISTAR;
    }

    private List<Habitaciones> obtenerHabitaciones() {
        return servicio.getHabitaciones()
                .stream()
                .filter(habitacion -> !habitacion.isEliminado())
                .sorted(Comparator.comparing(Habitaciones::getNumero).reversed())
                .collect(Collectors.toList());
    }

    private HashMap<Long, LocalDateTime> obtenerFechasCreacionEnLocalDateTime() {
        HashMap<Long, LocalDateTime> fechasCreacion = new HashMap<>();

        for (Habitaciones habitacion : obtenerHabitaciones()) {
            fechasCreacion.put(habitacion.getId_habitacion(), LocalDateTime.parse(habitacion.getFecha_creacion()));
        }

        return fechasCreacion;
    }
}
