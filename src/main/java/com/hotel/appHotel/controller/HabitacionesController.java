package com.hotel.appHotel.controller;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.hotel.appHotel.model.Habitaciones;
import com.hotel.appHotel.service.HabitacionesService;
import com.hotel.appHotel.service.PdfServiceHabitaciones;

@Controller
@RequestMapping(value = "/habitaciones")
public class HabitacionesController {

    private static final String CARPETA_BASE = "trabajadores_templates/";
    private static final String VIEW_LISTAR = CARPETA_BASE + "habitaciones";

    @Autowired
    private HabitacionesService servicio;

    @Autowired
    private PdfServiceHabitaciones pdfServiceHabitaciones;

    @GetMapping
    public String listarHabitaciones(Model modelo) {
        modelo.addAttribute("habitaciones", obtenerHabitaciones());

        return VIEW_LISTAR;
    }

    @GetMapping("/exportar-pdf")
    public ResponseEntity<byte[]> exportarVentasPdf() {
        byte[] pdf = pdfServiceHabitaciones.generarPdfHabitaciones(obtenerHabitaciones());

        if (pdf == null) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=habitaciones.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    private List<Habitaciones> obtenerHabitaciones() {
        return servicio.getHabitaciones()
                .stream()
                .filter(habitaciones -> !habitaciones.isEliminado())
                .sorted(Comparator.comparing(Habitaciones::getNumero))
                .collect(Collectors.toList());
    }
}
