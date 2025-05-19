package com.hotel.appHotel.controller;

import java.time.LocalDate;
import java.util.Comparator;
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
    public String listarVentasPorPagina(@RequestParam Map<String, Object> params, Model model) {
        int page = params.get("page") != null ? (Integer.parseInt(params.get("page").toString()) - 1) : 0;

        PageRequest pageRequest = PageRequest.of(page, 20);

        Page<Ventas> pageVentas = servicio.getVentasNoEliminadas(pageRequest);

        int totalPages = pageVentas.getTotalPages();

        if (totalPages > 0) {
            List<Integer> pages = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pages", pages);
        }

        model.addAttribute("ventas", pageVentas.getContent());
        model.addAttribute("actualPage", page + 1);
        model.addAttribute("nextPage", page + 2);
        model.addAttribute("prevPage", page);
        model.addAttribute("lastPage", totalPages);

        return VIEW_LISTAR;
    }

    @GetMapping("/exportar-pdf")
    public ResponseEntity<byte[]> exportarVentasPdf(@RequestParam(defaultValue = "1") int page) {
        int pageSize = 20;
        PageRequest pageable = PageRequest.of(page - 1, pageSize);

        Page<Ventas> ventasPage = servicio.getVentasNoEliminadas(pageable);

        byte[] pdf = pdfService.generarPdfVentas(ventasPage.getContent());

        if (pdf == null) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=ventas.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    @GetMapping("/reporte-por-fecha-pdf")
    public ResponseEntity<byte[]> exportarVentasSegunFechaPdf(
            @RequestParam(value = "fecha", required = false) String fecha) {
        LocalDate fechaFiltro;

        if (fecha != null && !fecha.isBlank()) {
            try {
                fechaFiltro = LocalDate.parse(fecha);
            } catch (Exception e) {
                fechaFiltro = LocalDate.now(); // En caso de error de formato
            }
        } else {
            fechaFiltro = LocalDate.now(); // Si no se envió parámetro
        }

        LocalDate finalFechaFiltro = fechaFiltro;

        List<Ventas> ventasDelDia = servicio.getVentas()
                .stream()
                .filter(venta -> !venta.isEliminado())
                .filter(venta -> {
                    LocalDate entrada = LocalDate.parse(venta.getFecha_entrada().substring(0, 10));
                    LocalDate salida = LocalDate.parse(venta.getFecha_salida().substring(0, 10));
                    
                    return (entrada.isEqual(finalFechaFiltro) || entrada.isBefore(finalFechaFiltro))
                            && (salida.isEqual(finalFechaFiltro) || salida.isAfter(finalFechaFiltro));
                })
                .sorted(Comparator.comparing(Ventas::getId_venta).reversed())
                .collect(Collectors.toList());

        byte[] pdf = pdfService.generarPdfVentas(ventasDelDia);

        if (pdf == null) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=reporte_" + finalFechaFiltro + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
}
