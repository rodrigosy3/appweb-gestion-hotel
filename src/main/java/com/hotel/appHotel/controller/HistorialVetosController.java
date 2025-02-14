package com.hotel.appHotel.controller;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import com.hotel.appHotel.model.HistorialVetos;
import com.hotel.appHotel.service.HistorialVetosService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = "/historialVetos")
public class HistorialVetosController {

    private static final String CARPETA_BASE = "trabajadores_templates/";
    private static final String VIEW_LISTAR = CARPETA_BASE + "historialVetos";

    @Autowired
    private HistorialVetosService historialVetosServicio;

    @GetMapping
    public String listarHistorialVetos(Model modelo) {
        List<HistorialVetos> historialVetosDesc = historialVetosServicio.getHistorialVetos().stream()
                .sorted(Comparator.comparing(HistorialVetos::getId_historial_veto).reversed())
                .collect(Collectors.toList());

        modelo.addAttribute("historialVetos", historialVetosDesc);

        return VIEW_LISTAR;
    }
}