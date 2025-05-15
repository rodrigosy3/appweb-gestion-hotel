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
import com.hotel.appHotel.model.HabitacionesCaracteristicas;
import com.hotel.appHotel.model.HabitacionesContenido;
import com.hotel.appHotel.service.HabitacionesCaracteristicasService;
import com.hotel.appHotel.service.HabitacionesContenidoService;
import com.hotel.appHotel.service.HabitacionesService;

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
        modelo.addAttribute("habitacionesContenido", obtenerHabitacionesContenido());
        modelo.addAttribute("fechasCreacion", obtenerFechasCreacionEnLocalDateTime());

        return VIEW_LISTAR;
    }

    @GetMapping("/nuevo")
    public String nuevaHabitacionContenidoForm(Model modelo, RedirectAttributes redirectAttributes) {
        HabitacionesContenido habitacionContenido = new HabitacionesContenido();
        List<Habitaciones> habitaciones = obtenerHabitaciones();
        List<HabitacionesCaracteristicas> habitacionesCaracteristica = obtenerHabitacionesCaracteristica();

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
        modelo.addAttribute("habitacionesContenido", obtenerHabitacionesContenido());
        modelo.addAttribute("fechasCreacion", obtenerFechasCreacionEnLocalDateTime());
        modelo.addAttribute("habitacionContenido", habitacionContenido);

        return VIEW_NUEVO;
    }

    @PostMapping
    public String crearHabitacionContenido(
            @ModelAttribute("habitacionContenido") HabitacionesContenido habitacionContenido) {
        servicio.createHabitacionContenido(habitacionContenido);

        return REDIRECT_LISTAR;
    }

    @GetMapping("/editar/{id}")
    public String editarHabitacionContenidoForm(@PathVariable Long id, Model modelo,
            RedirectAttributes redirectAttributes) {
        List<Habitaciones> habitaciones = obtenerHabitaciones();
        List<HabitacionesCaracteristicas> habitacionesCaracteristica = obtenerHabitacionesCaracteristica();

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
        modelo.addAttribute("habitacionesContenido", obtenerHabitacionesContenido());
        modelo.addAttribute("fechasCreacion", obtenerFechasCreacionEnLocalDateTime());
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
                .setFecha_creacion(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")));

        servicio.updateHabitacionContenido(habitacionContenidoExistente);

        return REDIRECT_LISTAR;
    }

    @PostMapping("/eliminar/{id}")
    public String eliminarHabitacionContenido(@PathVariable Long id) {
        HabitacionesContenido habitacionContenidoExistente = servicio.getHabitacionContenidoById(id);

        habitacionContenidoExistente.setEliminado(true);

        servicio.updateHabitacionContenido(habitacionContenidoExistente);

        return REDIRECT_LISTAR;
    }

    private List<HabitacionesContenido> obtenerHabitacionesContenido() {
        return servicio.getHabitacionesContenido()
                .stream()
                .filter(habitacionContenido -> !habitacionContenido.isEliminado())
                .sorted(Comparator.comparing(HabitacionesContenido::getId_habitacion_contenido).reversed())
                .collect(Collectors.toList());
    }

    private List<Habitaciones> obtenerHabitaciones() {
        return habitacionesServicio.getHabitaciones()
                .stream()
                .filter(habitacion -> !habitacion.isEliminado())
                .sorted(Comparator.comparing(Habitaciones::getNumero))
                .collect(Collectors.toList());
    }

    private List<HabitacionesCaracteristicas> obtenerHabitacionesCaracteristica() {
        return habitacionesCaracteristicasServicio.getHabitacionesCaracteristica()
                .stream()
                .filter(habitacionCaracteristica -> !habitacionCaracteristica.isEliminado())
                .collect(Collectors.toList());
    }

    private HashMap<Long, LocalDateTime> obtenerFechasCreacionEnLocalDateTime() {
        HashMap<Long, LocalDateTime> fechasCreacion = new HashMap<>();

        for (HabitacionesContenido habitacionContenido : obtenerHabitacionesContenido()) {
            fechasCreacion.put(habitacionContenido.getId_habitacion_contenido(), LocalDateTime.parse(habitacionContenido.getFecha_creacion()));
        }

        return fechasCreacion;
    }
}
