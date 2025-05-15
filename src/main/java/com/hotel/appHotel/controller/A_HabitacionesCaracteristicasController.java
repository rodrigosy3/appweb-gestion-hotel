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

import com.hotel.appHotel.model.HabitacionesCaracteristicas;
import com.hotel.appHotel.service.HabitacionesCaracteristicasService;

@Controller
@RequestMapping(value = "/admin/habitacionesCaracteristicas")
public class A_HabitacionesCaracteristicasController {

    private static final String CARPETA_BASE = "templates_habitacionesCaracteristicas/";
    private static final String VIEW_LISTAR = CARPETA_BASE + "habitacionesCaracteristicas";
    private static final String VIEW_NUEVO = CARPETA_BASE + "form_nuevo_habitacionCaracteristica";
    private static final String VIEW_EDITAR = CARPETA_BASE + "form_editar_habitacionCaracteristica";
    private static final String REDIRECT_LISTAR = "redirect:/admin/habitacionesCaracteristicas";

    @Autowired
    private HabitacionesCaracteristicasService servicio;

    @GetMapping
    public String listarHabitacionesCaracteristicas(Model modelo) {
        modelo.addAttribute("habitacionesCaracteristicas", obtenerHabitacionesCaracteristicas());
        modelo.addAttribute("fechasCreacion", obtenerFechasCreacionEnLocalDateTime());

        return VIEW_LISTAR;
    }

    @GetMapping("/nuevo")
    public String nuevaHabitacionCaracteristicaForm(Model modelo) {
        HabitacionesCaracteristicas habitacionCaracteristica = new HabitacionesCaracteristicas();

        modelo.addAttribute("habitacionesCaracteristicas", obtenerHabitacionesCaracteristicas());
        modelo.addAttribute("fechasCreacion", obtenerFechasCreacionEnLocalDateTime());
        modelo.addAttribute("habitacionCaracteristica", habitacionCaracteristica);

        return VIEW_NUEVO;
    }

    @PostMapping
    public String crearHabitacionCaracteristica(
            @ModelAttribute("habitacionCaracteristica") HabitacionesCaracteristicas habitacionCaracteristica) {
        habitacionCaracteristica.setNombre(habitacionCaracteristica.getNombre().toUpperCase());
        habitacionCaracteristica.setMarca(habitacionCaracteristica.getMarca().toUpperCase());

        servicio.createHabitacionCaracteristica(habitacionCaracteristica);

        return REDIRECT_LISTAR;
    }

    @GetMapping("/editar/{id}")
    public String editarHabitacionCaracteristicaForm(@PathVariable Long id, Model modelo) {
        modelo.addAttribute("habitacionesCaracteristicas", obtenerHabitacionesCaracteristicas());
        modelo.addAttribute("fechasCreacion", obtenerFechasCreacionEnLocalDateTime());
        modelo.addAttribute("habitacionCaracteristica", servicio.getHabitacionCaracteristicaById(id));

        return VIEW_EDITAR;
    }

    @PostMapping("/{id}")
    public String actualizarHabitacionCaracteristica(@PathVariable Long id,
            @ModelAttribute("habitacionCaracteristica") HabitacionesCaracteristicas habitacionCaracteristica,
            Model modelo) {
        HabitacionesCaracteristicas habitacionCaracteristicaExistente = servicio.getHabitacionCaracteristicaById(id);

        habitacionCaracteristicaExistente.setNombre(habitacionCaracteristica.getNombre().toUpperCase());
        habitacionCaracteristicaExistente.setMarca(habitacionCaracteristica.getMarca().toUpperCase());
        habitacionCaracteristicaExistente.setDescripcion(habitacionCaracteristica.getDescripcion());
        habitacionCaracteristicaExistente.setPrecio(habitacionCaracteristica.getPrecio());

        habitacionCaracteristicaExistente
                .setFecha_creacion(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")));

        servicio.updateHabitacionCaracteristica(habitacionCaracteristicaExistente);

        return REDIRECT_LISTAR;
    }

    @PostMapping("/eliminar/{id}")
    public String eliminarHabitacionCaracteristica(@PathVariable Long id) {
        HabitacionesCaracteristicas habitacionCaracteristicaExistente = servicio.getHabitacionCaracteristicaById(id);

        habitacionCaracteristicaExistente.setEliminado(true);

        servicio.updateHabitacionCaracteristica(habitacionCaracteristicaExistente);

        return REDIRECT_LISTAR;
    }

    private List<HabitacionesCaracteristicas> obtenerHabitacionesCaracteristicas() {
        return servicio.getHabitacionesCaracteristica()
                .stream()
                .filter(habitacionCaracteristica -> !habitacionCaracteristica.isEliminado())
                .sorted(Comparator.comparing(HabitacionesCaracteristicas::getId_habitacion_caracteristica).reversed())
                .collect(Collectors.toList());
    }

    private HashMap<Long, LocalDateTime> obtenerFechasCreacionEnLocalDateTime() {
        HashMap<Long, LocalDateTime> fechasCreacion = new HashMap<>();

        for (HabitacionesCaracteristicas habitacionCaracteristica : obtenerHabitacionesCaracteristicas()) {
            fechasCreacion.put(habitacionCaracteristica.getId_habitacion_caracteristica(), LocalDateTime.parse(habitacionCaracteristica.getFecha_creacion()));
        }

        return fechasCreacion;
    }
}
