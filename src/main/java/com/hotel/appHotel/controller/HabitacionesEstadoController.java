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

import com.hotel.appHotel.model.HabitacionesEstado;
import com.hotel.appHotel.service.HabitacionesEstadoService;

import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequestMapping(value = "/admin/habitacionesEstado")
public class HabitacionesEstadoController {

    @Autowired
    private HabitacionesEstadoService servicio;

    @GetMapping
    public String listarHabitacionesEstado(Model modelo) {
        modelo.addAttribute("habitacionesEstado", servicio.getHabitacionesEstado());
        
        return "templates_habitacionesEstado/habitacionesEstado";
    }
    
    @GetMapping("/nuevo")
    public String nuevaHabitacionEstadoForm(Model modelo) {
        HabitacionesEstado habitacionEstado = new HabitacionesEstado();
        
        modelo.addAttribute("habitacionesEstado", servicio.getHabitacionesEstado());
        modelo.addAttribute("habitacionEstado", habitacionEstado);
        
        return "templates_habitacionesEstado/form_nuevo_habitacionEstado";
    }

    @PostMapping
    public String crearHabitacionEstado(@ModelAttribute("habitacionEstado") HabitacionesEstado habitacionEstado) {
        habitacionEstado.setEstado(habitacionEstado.getEstado().toUpperCase());
        servicio.createHabitacionEstado(habitacionEstado);
        
        return "redirect:/admin/habitacionesEstado";
    }
    
    @GetMapping("/editar/{id}")
    public String editarHabitacionEstadoForm(@PathVariable Long id, Model modelo) {
        modelo.addAttribute("habitacionesEstado", servicio.getHabitacionesEstado());
        modelo.addAttribute("habitacionEstado", servicio.getHabitacionEstadoById(id));

        return "templates_habitacionesEstado/form_editar_habitacionEstado";
    }

    @PostMapping("/{id}")
    public String actualizarHabitacionEstado(@PathVariable Long id, @ModelAttribute("habitacionEstado") HabitacionesEstado habitacionEstado , Model modelo) {
        HabitacionesEstado habitacionEstadoExistente = servicio.getHabitacionEstadoById(id);
        
        habitacionEstadoExistente.setEstado(habitacionEstado.getEstado().toUpperCase());
        habitacionEstadoExistente.setMensaje(habitacionEstado.getMensaje());
        habitacionEstadoExistente.setFecha_creacion(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        
        servicio.updateHabitacionEstado(habitacionEstadoExistente);

        return "redirect:/admin/habitacionesEstado";
    }
    
    @GetMapping("/{id}")
    public String eliminarHabitacionEstado(@PathVariable Long id) {
        servicio.deleteHabitacionEstado(id);

        return "redirect:/admin/habitacionesEstado";
    }
}
