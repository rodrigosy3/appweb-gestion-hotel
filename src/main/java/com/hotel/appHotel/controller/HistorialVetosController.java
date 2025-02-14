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

import com.hotel.appHotel.model.HistorialVetos;
import com.hotel.appHotel.service.HistorialVetosService;
import com.hotel.appHotel.service.PdfServiceHistorialVetos;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = "/historialVetos")
public class HistorialVetosController {

    private static final String CARPETA_BASE = "trabajadores_templates/";
    private static final String VIEW_LISTAR = CARPETA_BASE + "historialVetos";

    @Autowired
    private HistorialVetosService servicio;

    @Autowired
    private PdfServiceHistorialVetos pdfServiceHistorialVetos;

    @GetMapping
    public String listarHistorialVetos(Model modelo) {
        List<HistorialVetos> historialVetosDesc = servicio.getHistorialVetos().stream()
                .sorted(Comparator.comparing(HistorialVetos::getId_historial_veto).reversed())
                .collect(Collectors.toList());

        modelo.addAttribute("historialVetos", historialVetosDesc);

        return VIEW_LISTAR;
    }

    @GetMapping("/exportar-pdf")
    public ResponseEntity<byte[]> exportarVentasPdf() {
        List<HistorialVetos> historialVetos = servicio.getHistorialVetos().stream()
                .sorted(Comparator.comparing(HistorialVetos::getId_historial_veto).reversed())
                .collect(Collectors.toList());
        // Obtener las ventas desde el servicio
        byte[] pdf = pdfServiceHistorialVetos.generarPdfHistorialVetos(historialVetos);

        if (pdf == null) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=historial-vetos.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
}