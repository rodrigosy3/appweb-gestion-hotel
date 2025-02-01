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
import com.hotel.appHotel.model.HabitacionesCaracteristicas;
import com.hotel.appHotel.model.HabitacionesContenido;
import com.hotel.appHotel.service.HabitacionesCaracteristicasService;
import com.hotel.appHotel.service.HabitacionesContenidoService;
import com.hotel.appHotel.service.HabitacionesService;

import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequestMapping(value = "/admin/habitacionesContenido")
public class A_HabitacionesContenidoController {
    
    private static final String CARPETA_BASE = "templates_habitacionesContenido/";
    private static final String VIEW_LISTAR = CARPETA_BASE + "habitacionesContenido";
    private static final String VIEW_NUEVO = CARPETA_BASE + "form_nuevo_habitacionContenido";
    private static final String VIEW_EDITAR = CARPETA_BASE + "form_editar_habitacionContenido";
    private static final String REDIRECT_LISTAR = "redirect:/admin/habitacionesContenido";

    @Autowired
    private HabitacionesContenidoService servicio;

    @Autowired
    private HabitacionesService habitacionesServicio;

    @Autowired
    private HabitacionesCaracteristicasService habitacionesCaracteristicasServicio;

    @GetMapping
    public String listarHabitacionesContenido(Model modelo) {
        modelo.addAttribute("habitacionesContenido", servicio.getHabitacionesContenido());

        return VIEW_LISTAR;
    }

    @GetMapping("/nuevo")
    public String nuevaHabitacionContenidoForm(Model modelo, RedirectAttributes redirectAttributes) {
        HabitacionesContenido habitacionContenido = new HabitacionesContenido();
        List<Habitaciones> habitaciones = habitacionesServicio.getHabitaciones();
        List<HabitacionesCaracteristicas> habitacionesCaracteristica = habitacionesCaracteristicasServicio.getHabitacionesCaracteristica();

        if (habitaciones.isEmpty()) {
            redirectAttributes.addFlashAttribute("error",
                    "No hay HABITACIONES necesarias para crear un nuevo registro.");

            return REDIRECT_LISTAR;
        }

        if (habitacionesCaracteristica.isEmpty()) {
            redirectAttributes.addFlashAttribute("error",
                    "No hay registros de HABITACIONES CARACTERISTICAS necesarias para crear un nuevo registro aquí.");

            return REDIRECT_LISTAR;
        }

        modelo.addAttribute("habitaciones", habitaciones);
        modelo.addAttribute("habitacionesCaracteristica", habitacionesCaracteristica);
        modelo.addAttribute("habitacionesContenido", servicio.getHabitacionesContenido());
        modelo.addAttribute("habitacionContenido", habitacionContenido);

        return VIEW_NUEVO;
    }

    @PostMapping
    public String crearHabitacionContenido(@ModelAttribute("habitacionContenido") HabitacionesContenido habitacionContenido) {
        servicio.createHabitacionContenido(habitacionContenido);

        return REDIRECT_LISTAR;
    }

    @GetMapping("/editar/{id}")
    public String editarHabitacionContenidoForm(@PathVariable Long id, Model modelo, RedirectAttributes redirectAttributes) {
        List<Habitaciones> habitaciones = habitacionesServicio.getHabitaciones();
        List<HabitacionesCaracteristicas> habitacionesCaracteristica = habitacionesCaracteristicasServicio.getHabitacionesCaracteristica();

        if (habitaciones.isEmpty()) {
            redirectAttributes.addFlashAttribute("error",
                    "No hay HABITACIONES necesarias para crear un nuevo registro.");

            return REDIRECT_LISTAR;
        }

        if (habitacionesCaracteristica.isEmpty()) {
            redirectAttributes.addFlashAttribute("error",
                    "No hay registros de HABITACIONES CARACTERISTICAS necesarias para crear un nuevo registro aquí.");

            return REDIRECT_LISTAR;
        }

        modelo.addAttribute("habitaciones", habitaciones);
        modelo.addAttribute("habitacionesCaracteristica", habitacionesCaracteristica);
        modelo.addAttribute("habitacionesContenido", servicio.getHabitacionesContenido());
        modelo.addAttribute("habitacionContenido", servicio.getHabitacionContenidoById(id));

        return VIEW_EDITAR;
    }

    @PostMapping("/{id}")
    public String actualizarHabitacionContenido(@PathVariable Long id,
            @ModelAttribute("habitacionContenido") HabitacionesContenido habitacionContenido, Model modelo) {
        HabitacionesContenido habitacionContenidoExistente = servicio.getHabitacionContenidoById(id);

        habitacionContenidoExistente.setHabitacion(habitacionContenido.getHabitacion());
        habitacionContenidoExistente.setCaracteristica(habitacionContenido.getCaracteristica());
        habitacionContenidoExistente.setEstado_caracteristica(habitacionContenido.getEstado_caracteristica());
        habitacionContenidoExistente.setRazon_estado(habitacionContenido.getRazon_estado());

        habitacionContenidoExistente
                .setFecha_creacion(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        servicio.updateHabitacionContenido(habitacionContenidoExistente);

        return REDIRECT_LISTAR;
    }

    @GetMapping("/{id}")
    public String eliminarHabitacionContenido(@PathVariable Long id) {
        servicio.deleteHabitacionContenido(id);

        return REDIRECT_LISTAR;
    }
}
