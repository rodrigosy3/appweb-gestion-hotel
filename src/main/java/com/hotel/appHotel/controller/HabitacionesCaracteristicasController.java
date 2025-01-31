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

import com.hotel.appHotel.model.HabitacionesCaracteristicas;
import com.hotel.appHotel.service.HabitacionesCaracteristicasService;

import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequestMapping(value = "/admin/habitacionesCaracteristicas")
public class HabitacionesCaracteristicasController {

    private static final String CARPETA_BASE = "templates_habitacionesCaracteristicas/";
    private static final String VIEW_LISTAR = CARPETA_BASE + "habitacionesCaracteristicas";
    private static final String VIEW_NUEVO = CARPETA_BASE + "form_nuevo_habitacionCaracteristica";
    private static final String VIEW_EDITAR = CARPETA_BASE + "form_editar_habitacionCaracteristica";
    private static final String REDIRECT_LISTAR = "redirect:/admin/habitacionesCaracteristicas";

    @Autowired
    private HabitacionesCaracteristicasService servicio;

    @GetMapping
    public String listarHabitacionesCaracteristicas(Model modelo) {
        modelo.addAttribute("habitacionesCaracteristicas", servicio.getHabitacionesCaracteristica());
        
        return VIEW_LISTAR;
    }
    
    @GetMapping("/nuevo")
    public String nuevaHabitacionCaracteristicaForm(Model modelo) {
        HabitacionesCaracteristicas habitacionCaracteristica = new HabitacionesCaracteristicas();
        
        modelo.addAttribute("habitacionesCaracteristicas", servicio.getHabitacionesCaracteristica());
        modelo.addAttribute("habitacionCaracteristica", habitacionCaracteristica);
        
        return VIEW_NUEVO;
    }

    @PostMapping
    public String crearHabitacionCaracteristica(@ModelAttribute("habitacionCaracteristica") HabitacionesCaracteristicas habitacionCaracteristica) {
        habitacionCaracteristica.setNombre(habitacionCaracteristica.getNombre().toUpperCase());
        habitacionCaracteristica.setMarca(habitacionCaracteristica.getMarca().toUpperCase());

        servicio.createHabitacionCaracteristica(habitacionCaracteristica);
        
        return REDIRECT_LISTAR;
    }
    
    @GetMapping("/editar/{id}")
    public String editarHabitacionCaracteristicaForm(@PathVariable Long id, Model modelo) {
        modelo.addAttribute("habitacionesCaracteristicas", servicio.getHabitacionesCaracteristica());
        modelo.addAttribute("habitacionCaracteristica", servicio.getHabitacionCaracteristicaById(id));

        return VIEW_EDITAR;
    }

    @PostMapping("/{id}")
    public String actualizarHabitacionCaracteristica(@PathVariable Long id, @ModelAttribute("habitacionCaracteristica") HabitacionesCaracteristicas habitacionCaracteristica , Model modelo) {
        HabitacionesCaracteristicas habitacionCaracteristicaExistente = servicio.getHabitacionCaracteristicaById(id);
        
        habitacionCaracteristicaExistente.setNombre(habitacionCaracteristica.getNombre().toUpperCase());
        habitacionCaracteristicaExistente.setMarca(habitacionCaracteristica.getMarca().toUpperCase());
        habitacionCaracteristicaExistente.setPrecio(habitacionCaracteristica.getPrecio());

        habitacionCaracteristicaExistente.setFecha_creacion(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        
        servicio.updateHabitacionCaracteristica(habitacionCaracteristicaExistente);

        return REDIRECT_LISTAR;
    }
    
    @GetMapping("/{id}")
    public String eliminarHabitacionCaracteristica(@PathVariable Long id) {
        servicio.deleteHabitacionCaracteristica(id);

        return REDIRECT_LISTAR;
    }
}
