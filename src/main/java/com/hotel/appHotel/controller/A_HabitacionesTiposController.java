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

import com.hotel.appHotel.model.HabitacionesTipos;
import com.hotel.appHotel.service.HabitacionesTiposService;

import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequestMapping(value = "/admin/habitacionesTipos")
public class A_HabitacionesTiposController {

    @Autowired
    private HabitacionesTiposService servicio;

    @GetMapping
    public String listarHabitacionesTipos(Model modelo) {
        modelo.addAttribute("habitacionesTipos", servicio.getHabitacionesTipos());
        
        return "templates_habitacionesTipos/habitacionesTipos";
    }
    
    @GetMapping("/nuevo")
    public String nuevaHabitacionTipoForm(Model modelo) {
        HabitacionesTipos habitacionTipo = new HabitacionesTipos();
        
        modelo.addAttribute("habitacionesTipos", servicio.getHabitacionesTipos());
        modelo.addAttribute("habitacionTipo", habitacionTipo);
        
        return "templates_habitacionesTipos/form_nuevo_habitacionTipo";
    }

    @PostMapping
    public String crearHabitacionTipo(@ModelAttribute("habitacionTipo") HabitacionesTipos habitacionTipo) {
        habitacionTipo.setNombre_tipo(habitacionTipo.getNombre_tipo().toUpperCase());
        habitacionTipo.setAbreviacion_tipo(habitacionTipo.getAbreviacion_tipo().toUpperCase());

        servicio.createHabitacionTipo(habitacionTipo);
        
        return "redirect:/admin/habitacionesTipos";
    }
    
    @GetMapping("/editar/{id}")
    public String editarHabitacionTipoForm(@PathVariable Long id, Model modelo) {
        modelo.addAttribute("habitacionesTipos", servicio.getHabitacionesTipos());
        modelo.addAttribute("habitacionTipo", servicio.getHabitacionTipoById(id));

        return "templates_habitacionesTipos/form_editar_habitacionTipo";
    }

    @PostMapping("/{id}")
    public String actualizarHabitacionTipo(@PathVariable Long id, @ModelAttribute("habitacionTipo") HabitacionesTipos habitacionTipo , Model modelo) {
        HabitacionesTipos habitacionTipoExistente = servicio.getHabitacionTipoById(id);
        
        habitacionTipoExistente.setNombre_tipo(habitacionTipo.getNombre_tipo().toUpperCase());
        habitacionTipoExistente.setAbreviacion_tipo(habitacionTipo.getAbreviacion_tipo().toUpperCase());

        habitacionTipoExistente.setFecha_creacion(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        
        servicio.updateHabitacionTipo(habitacionTipoExistente);

        return "redirect:/admin/habitacionesTipos";
    }
    
    @GetMapping("/{id}")
    public String eliminarHabitacionTipo(@PathVariable Long id) {
        servicio.deleteHabitacionTipo(id);

        return "redirect:/admin/habitacionesTipos";
    }
}
