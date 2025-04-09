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

import com.hotel.appHotel.model.HabitacionesTipos;
import com.hotel.appHotel.service.HabitacionesTiposService;

import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequestMapping(value = "/admin/habitacionesTipos")
public class A_HabitacionesTiposController {

    private static final String CARPETA_BASE = "templates_habitacionesTipos/";
    private static final String VIEW_LISTAR = CARPETA_BASE + "habitacionesTipos";
    private static final String VIEW_NUEVO = CARPETA_BASE + "form_nuevo_habitacionTipo";
    private static final String VIEW_EDITAR = CARPETA_BASE + "form_editar_habitacionTipo";
    private static final String REDIRECT_LISTAR = "redirect:/admin/habitacionesTipos";

    @Autowired
    private HabitacionesTiposService servicio;

    @GetMapping
    public String listarHabitacionesTipos(Model modelo) {
        modelo.addAttribute("habitacionesTipos", obtenerHabitacionesTipos());

        return VIEW_LISTAR;
    }

    @GetMapping("/nuevo")
    public String nuevaHabitacionTipoForm(Model modelo) {
        HabitacionesTipos habitacionTipo = new HabitacionesTipos();

        modelo.addAttribute("habitacionesTipos", obtenerHabitacionesTipos());
        modelo.addAttribute("habitacionTipo", habitacionTipo);

        return VIEW_NUEVO;
    }

    @PostMapping
    public String crearHabitacionTipo(@ModelAttribute("habitacionTipo") HabitacionesTipos habitacionTipo) {
        habitacionTipo.setNombre_tipo(habitacionTipo.getNombre_tipo().toUpperCase());
        habitacionTipo.setAbreviacion_tipo(habitacionTipo.getAbreviacion_tipo().toUpperCase());

        servicio.createHabitacionTipo(habitacionTipo);

        return REDIRECT_LISTAR;
    }

    @GetMapping("/editar/{id}")
    public String editarHabitacionTipoForm(@PathVariable Long id, Model modelo) {
        modelo.addAttribute("habitacionesTipos", obtenerHabitacionesTipos());
        modelo.addAttribute("habitacionTipo", servicio.getHabitacionTipoById(id));

        return VIEW_EDITAR;
    }

    @PostMapping("/{id}")
    public String actualizarHabitacionTipo(@PathVariable Long id,
            @ModelAttribute("habitacionTipo") HabitacionesTipos habitacionTipo, Model modelo) {
        HabitacionesTipos habitacionTipoExistente = servicio.getHabitacionTipoById(id);

        habitacionTipoExistente.setNombre_tipo(habitacionTipo.getNombre_tipo().toUpperCase());
        habitacionTipoExistente.setAbreviacion_tipo(habitacionTipo.getAbreviacion_tipo().toUpperCase());

        habitacionTipoExistente
                .setFecha_creacion(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        servicio.updateHabitacionTipo(habitacionTipoExistente);

        return REDIRECT_LISTAR;
    }

    @PostMapping("/eliminar/{id}")
    public String eliminarHabitacionTipo(@PathVariable Long id) {
        HabitacionesTipos habitacionTipoExistente = servicio.getHabitacionTipoById(id);

        habitacionTipoExistente.setEliminado(true);

        servicio.updateHabitacionTipo(habitacionTipoExistente);

        return REDIRECT_LISTAR;
    }

    private List<HabitacionesTipos> obtenerHabitacionesTipos() {
        return servicio.getHabitacionesTipos()
                .stream()
                .filter(habitacionTipo -> !habitacionTipo.isEliminado())
                .sorted(Comparator.comparing(HabitacionesTipos::getId_habitacion_tipo).reversed())
                .collect(Collectors.toList());
    }
}
