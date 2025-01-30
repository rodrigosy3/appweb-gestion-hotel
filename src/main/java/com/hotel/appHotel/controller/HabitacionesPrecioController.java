package com.hotel.appHotel.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

// import com.hotel.appHotel.model.Habitaciones;
import com.hotel.appHotel.model.HabitacionesPrecio;
import com.hotel.appHotel.service.HabitacionesPrecioService;

import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequestMapping(value = "/admin/habitacionesPrecio")
public class HabitacionesPrecioController {

    @Autowired
    private HabitacionesPrecioService servicio;

    // @Autowired
    // private HabitacionesService habitacionesServicio;

    @GetMapping
    public String listarHabitacionesPrecio(Model modelo) {
        modelo.addAttribute("HabitacionesPrecio", servicio.getHabitacionesPrecio());

        return "templates_habitacionesPrecio/habitacionesPrecio";
    }

    @GetMapping("/nuevo")
    public String nuevaHabitacionPrecioForm(Model modelo) {
        HabitacionesPrecio habitacionPrecio = new HabitacionesPrecio();
        // Habitaciones habitaciones = habitacionesServicio.getHabitaciones();

        // if (habitaciones.) {

        // }

        // modelo.addAttribute("habitaciones", habitaciones);
        modelo.addAttribute("HabitacionesPrecio", servicio.getHabitacionesPrecio());
        modelo.addAttribute("habitacionPrecio", habitacionPrecio);

        return "templates_HabitacionesPrecio/form_nuevo_habitacionPrecio";
    }

    @PostMapping
    public String crearHabitacionPrecio(@ModelAttribute("habitacionPrecio") HabitacionesPrecio habitacionPrecio) {
        servicio.createHabitacionPrecio(habitacionPrecio);

        return "redirect:/admin/habitacionesPrecio";
    }

    @GetMapping("/editar/{id}")
    public String editarHabitacionPrecioForm(@PathVariable Long id, Model modelo) {
        modelo.addAttribute("HabitacionesPrecio", servicio.getHabitacionesPrecio());
        modelo.addAttribute("habitacionPrecio", servicio.getHabitacionPrecioById(id));

        return "templates_habitacionesPrecio/form_editar_habitacionPrecio";
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

        return "redirect:/admin/habitacionesPrecio";
    }

    @GetMapping("/{id}")
    public String eliminarHabitacionPrecio(@PathVariable Long id) {
        servicio.deleteHabitacionPrecio(id);

        return "redirect:/admin/habitacionesPrecio";
    }
}
