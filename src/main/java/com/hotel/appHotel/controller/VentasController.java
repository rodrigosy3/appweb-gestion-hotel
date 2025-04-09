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

import com.hotel.appHotel.model.Ventas;
import com.hotel.appHotel.service.PdfServiceVentas;
import com.hotel.appHotel.service.VentasService;

@Controller
@RequestMapping(value = "/ventas")
public class VentasController {

    private static final String CARPETA_BASE = "trabajadores_templates/";
    private static final String VIEW_LISTAR = CARPETA_BASE + "ventas";

    @Autowired
    private VentasService servicio;

    @Autowired
    private PdfServiceVentas pdfService;

    @GetMapping
    public String listarVentas(Model modelo) {
        modelo.addAttribute("ventas", obtenerVentas());

        return VIEW_LISTAR;
    }

    @GetMapping("/exportar-pdf")
    public ResponseEntity<byte[]> exportarVentasPdf() {
        byte[] pdf = pdfService.generarPdfVentas(obtenerVentas());

        if (pdf == null) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=ventas.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    private List<Ventas> obtenerVentas() {
        return servicio.getVentas()
                .stream()
                .filter(venta -> !venta.isEliminado())
                .sorted(Comparator.comparing(Ventas::getId_venta).reversed())
                .collect(Collectors.toList());
    }
}
