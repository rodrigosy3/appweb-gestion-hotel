package com.hotel.appHotel.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
import com.hotel.appHotel.model.HabitacionesPrecio;
import com.hotel.appHotel.service.HabitacionesPrecioService;
import com.hotel.appHotel.service.HabitacionesService;

import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequestMapping(value = "/admin/habitacionesPrecio")
public class A_HabitacionesPrecioController {
    
    private static final String CARPETA_BASE = "templates_habitacionesPrecio/";
    private static final String VIEW_LISTAR = CARPETA_BASE + "habitacionesPrecio";
    private static final String VIEW_NUEVO = CARPETA_BASE + "form_nuevo_habitacionPrecio";
    private static final String VIEW_EDITAR = CARPETA_BASE + "form_editar_habitacionPrecio";
    private static final String REDIRECT_LISTAR = "redirect:/admin/habitacionesPrecio";

    @Autowired
    private HabitacionesPrecioService servicio;

    @Autowired
    private HabitacionesService habitacionesServicio;

    @GetMapping
    public String listarHabitacionesPrecio(Model modelo) {
        modelo.addAttribute("habitacionesPrecio", servicio.getHabitacionesPrecio());

        return VIEW_LISTAR;
    }

    @GetMapping("/nuevo")
    public String nuevaHabitacionPrecioForm(Model modelo, RedirectAttributes redirectAttributes) {
        HabitacionesPrecio habitacionPrecio = new HabitacionesPrecio();
        List<Habitaciones> habitaciones = habitacionesServicio.getHabitaciones();

        if (habitaciones.isEmpty()) {
            redirectAttributes.addFlashAttribute("error",
                    "No hay HABITACIONES necesarias para crear un nuevo registro de PRECIO.");

            return REDIRECT_LISTAR;
        }

        modelo.addAttribute("habitaciones", habitaciones);
        modelo.addAttribute("habitacionesPrecio", servicio.getHabitacionesPrecio());
        modelo.addAttribute("habitacionPrecio", habitacionPrecio);

        return VIEW_NUEVO;
    }

    @PostMapping
    public String crearHabitacionPrecio(@ModelAttribute("habitacionPrecio") HabitacionesPrecio habitacionPrecio) {
        servicio.createHabitacionPrecio(habitacionPrecio);

        return REDIRECT_LISTAR;
    }

    @GetMapping("/editar/{id}")
    public String editarHabitacionPrecioForm(@PathVariable Long id, Model modelo, RedirectAttributes redirectAttributes) {
        List<Habitaciones> habitaciones = habitacionesServicio.getHabitaciones();

        if (habitaciones.isEmpty()) {
            redirectAttributes.addFlashAttribute("error",
                    "No hay HABITACIONES necesarias para crear un nuevo registro de PRECIO.");

            return REDIRECT_LISTAR;
        }

        modelo.addAttribute("habitaciones", habitaciones);
        modelo.addAttribute("habitacionesPrecio", servicio.getHabitacionesPrecio());
        modelo.addAttribute("habitacionPrecio", servicio.getHabitacionPrecioById(id));

        return VIEW_EDITAR;
    }

    @PostMapping("/{id}")
    public String actualizarHabitacionPrecio(@PathVariable Long id,
            @ModelAttribute("habitacionPrecio") HabitacionesPrecio habitacionPrecio, Model modelo) {
        HabitacionesPrecio habitacionPrecioExistente = servicio.getHabitacionPrecioById(id);

        habitacionPrecioExistente.setHabitacion(habitacionPrecio.getHabitacion());
        habitacionPrecioExistente.setPrecio(habitacionPrecio.getPrecio());
        habitacionPrecioExistente.setTiempo_estadia(habitacionPrecio.getTiempo_estadia());

        habitacionPrecioExistente
                .setFecha_creacion(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        servicio.updateHabitacionPrecio(habitacionPrecioExistente);

        return REDIRECT_LISTAR;
    }

    @GetMapping("/{id}")
    public String eliminarHabitacionPrecio(@PathVariable Long id) {
        servicio.deleteHabitacionPrecio(id);

        return REDIRECT_LISTAR;
    }
}
