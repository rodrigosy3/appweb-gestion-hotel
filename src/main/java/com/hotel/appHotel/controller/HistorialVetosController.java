package com.hotel.appHotel.controller;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.hotel.appHotel.model.HistorialVetos;
import com.hotel.appHotel.service.HistorialVetosService;
import com.hotel.appHotel.service.PdfServiceHistorialVetos;

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
    public String listarHistorialVetosPorPagina(@RequestParam Map<String, Object> params, Model model) {
        int page = params.get("page") != null ? (Integer.parseInt(params.get("page").toString()) - 1) : 0;

        PageRequest pageRequest = PageRequest.of(page, 25);

        Page<HistorialVetos> pageUsuarios = servicio.getHistorialVetosNoEliminados(pageRequest);

        int totalPages = pageUsuarios.getTotalPages();

        if (totalPages > 0) {
            List<Integer> pages = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pages", pages);
        }

        model.addAttribute("historialVetos", pageUsuarios.getContent());
        model.addAttribute("actualPage", page + 1);
        model.addAttribute("nextPage", page + 2);
        model.addAttribute("prevPage", page);
        model.addAttribute("lastPage", totalPages);

        return VIEW_LISTAR;
    }

    @GetMapping("/exportar-pdf")
    public ResponseEntity<byte[]> exportarVentasPdf(@RequestParam(defaultValue = "1") int page) {
        int pageSize = 25;
        PageRequest pageable = PageRequest.of(page - 1, pageSize);

        Page<HistorialVetos> historialVetosPage = servicio.getHistorialVetosNoEliminados(pageable);

        byte[] pdf = pdfServiceHistorialVetos.generarPdfHistorialVetos(historialVetosPage.getContent());

        if (pdf == null) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=historial-vetos.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

}