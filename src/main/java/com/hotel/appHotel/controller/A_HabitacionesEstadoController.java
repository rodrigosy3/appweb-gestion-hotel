package com.hotel.appHotel.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.hotel.appHotel.model.HabitacionesEstado;
import com.hotel.appHotel.service.HabitacionesEstadoService;

import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequestMapping(value = "/admin/habitacionesEstado")
public class A_HabitacionesEstadoController {

    private static final String CARPETA_BASE = "templates_habitacionesEstado/";
    private static final String VIEW_LISTAR = CARPETA_BASE + "habitacionesEstado";
    private static final String VIEW_NUEVO = CARPETA_BASE + "form_nuevo_habitacionEstado";
    private static final String VIEW_EDITAR = CARPETA_BASE + "form_editar_habitacionEstado";
    private static final String REDIRECT_LISTAR = "redirect:/admin/habitacionesEstado";

    @Autowired
    private HabitacionesEstadoService servicio;

    @GetMapping
    public String listarHabitacionesEstado(Model modelo) {
        modelo.addAttribute("habitacionesEstado", obtenerHabitacionesEstado());

        return VIEW_LISTAR;
    }

    @GetMapping("/nuevo")
    public String nuevaHabitacionEstadoForm(Model modelo) {
        HabitacionesEstado habitacionEstado = new HabitacionesEstado();

        modelo.addAttribute("habitacionesEstado", obtenerHabitacionesEstado());
        modelo.addAttribute("habitacionEstado", habitacionEstado);

        return VIEW_NUEVO;
    }

    @PostMapping
    public String crearHabitacionEstado(@ModelAttribute("habitacionEstado") HabitacionesEstado habitacionEstado) {
        habitacionEstado.setEstado(habitacionEstado.getEstado().toUpperCase());
        servicio.createHabitacionEstado(habitacionEstado);

        return REDIRECT_LISTAR;
    }

    @GetMapping("/editar/{id}")
    public String editarHabitacionEstadoForm(@PathVariable Long id, Model modelo) {
        modelo.addAttribute("habitacionesEstado", obtenerHabitacionesEstado());
        modelo.addAttribute("habitacionEstado", servicio.getHabitacionEstadoById(id));

        return VIEW_EDITAR;
    }

    @PostMapping("/{id}")
    public String actualizarHabitacionEstado(@PathVariable Long id,
            @ModelAttribute("habitacionEstado") HabitacionesEstado habitacionEstado, Model modelo) {
        HabitacionesEstado habitacionEstadoExistente = servicio.getHabitacionEstadoById(id);

        habitacionEstadoExistente.setEstado(habitacionEstado.getEstado().toUpperCase());
        habitacionEstadoExistente.setMensaje(habitacionEstado.getMensaje());
        habitacionEstadoExistente
                .setFecha_creacion(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        servicio.updateHabitacionEstado(habitacionEstadoExistente);

        return REDIRECT_LISTAR;
    }

    @PostMapping("/eliminar/{id}")
    public String eliminarHabitacionEstado(@PathVariable Long id) {
        HabitacionesEstado habitacionEstadoExistente = servicio.getHabitacionEstadoById(id);

        habitacionEstadoExistente.setEliminado(true);

        servicio.updateHabitacionEstado(habitacionEstadoExistente);

        return REDIRECT_LISTAR;
    }

    private List<HabitacionesEstado> obtenerHabitacionesEstado() {
        return servicio.getHabitacionesEstado()
                .stream()
                .filter(habitacionEstado -> !habitacionEstado.isEliminado())
                .sorted(Comparator.comparing(HabitacionesEstado::getId_habitacion_estado).reversed())
                .collect(Collectors.toList());
    }
}
