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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hotel.appHotel.config.TicketPrinter;
import com.hotel.appHotel.model.Cajas;
import com.hotel.appHotel.model.Usuarios;
import com.hotel.appHotel.repository.CajasRepository;
import com.hotel.appHotel.repository.CredencialesRepository;
import com.hotel.appHotel.service.CajasService;
import com.hotel.appHotel.service.PdfServiceCajas;

@Controller
@RequestMapping(value = "/cajas")
public class CajasController {

    private static final String CARPETA_BASE = "trabajadores_templates/";
    private static final String VIEW_LISTAR = CARPETA_BASE + "cajas";

    @Autowired
    private CajasService servicio;

    @Autowired
    private PdfServiceCajas pdfService;

    @Autowired
    private CajasRepository repositorio;

    @Autowired
    private CredencialesRepository repositorioCredenciales;

    @GetMapping
    public String listarVentasPorPagina(@RequestParam Map<String, Object> params, Model model) {
        int page = params.get("page") != null ? (Integer.parseInt(params.get("page").toString()) - 1) : 0;

        PageRequest pageRequest = PageRequest.of(page, 20);

        Page<Cajas> pageCajas = servicio.getCajasNoEliminadas(pageRequest);

        int totalPages = pageCajas.getTotalPages();

        if (totalPages > 0) {
            List<Integer> pages = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pages", pages);
        }

        model.addAttribute("cajas", pageCajas.getContent());
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

        Page<Cajas> ventasPage = servicio.getCajasNoEliminadas(pageable);

        byte[] pdf = pdfService.generarPdfCajas(ventasPage.getContent());

        if (pdf == null) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=cajas.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    @GetMapping("/reporte-por-fecha-pdf")
    public ResponseEntity<byte[]> exportarCajasPorFechaPdf(
            @RequestParam(value = "fecha", required = false) String fecha) {
        // 1. Parsear el parámetro 'fecha', o usar hoy si no se suministra o hay error
        LocalDate fechaFiltro;
        if (fecha != null && !fecha.isBlank()) {
            try {
                fechaFiltro = LocalDate.parse(fecha);
            } catch (Exception e) {
                fechaFiltro = LocalDate.now();
            }
        } else {
            fechaFiltro = LocalDate.now();
        }
        LocalDate finalFechaFiltro = fechaFiltro;

        // 2. Obtener todas las cajas (no eliminadas) y filtrar por fechaRegistro ==
        // fechaFiltro
        List<Cajas> cajasDelDia = servicio.getCajas().stream()
                .filter(caja -> !caja.getEliminado()) // asumimos que el método isEliminado() existe
                .filter(caja -> {
                    // fechaRegistro viene como "yyyy-MM-dd'T'HH:mm:ss"
                    String fechaIso = caja.getFechaRegistro();
                    LocalDate fechaRegistro;
                    try {
                        fechaRegistro = LocalDate.parse(fechaIso.substring(0, 10));
                    } catch (Exception ex) {
                        return false;
                    }
                    return fechaRegistro.isEqual(finalFechaFiltro);
                })
                // Opcional: ordenar por ID descendente para mostrar los más recientes primero
                .sorted(Comparator.comparing(Cajas::getIdCaja).reversed())
                .collect(Collectors.toList());

        // 3. Generar el PDF con la lista filtrada
        byte[] pdf = pdfService.generarPdfCajas(cajasDelDia);
        if (pdf == null) {
            return ResponseEntity.badRequest().build();
        }

        // 4. Devolver el PDF como respuesta descargable
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=reporte_cajas_" + finalFechaFiltro + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    @GetMapping("/ticket-caja-por-fecha")
    @ResponseBody
    public ResponseEntity<String> imprimirTicketCajaPorFecha(@RequestParam(value = "fecha", required = false) String fecha) {
        try {
            // Obtener el usuario responsable
            String dni = SecurityContextHolder.getContext().getAuthentication().getName();
            Usuarios usuario = null;

            try {
                usuario = repositorioCredenciales.buscarPorDni(dni).map(c -> c.getUsuario()).orElse(null);
            } catch (Exception ignored) {
            }

            // Parsear la fecha o usar hoy como predeterminado
            LocalDate fechaFiltro = (fecha != null && !fecha.isBlank())
                    ? LocalDate.parse(fecha)
                    : LocalDate.now();

            // Obtener las cajas correspondientes a esa fecha
            List<Cajas> cajasDelDia = repositorio.obtenerCajasDelDia(String.valueOf(fechaFiltro));

            // Imprimir el ticket con la lógica centralizada
            TicketPrinter.imprimirTicketCajaDelDia(usuario, cajasDelDia, fechaFiltro);

            return ResponseEntity.ok("✅ Ticket impreso correctamente");

        } catch (Exception e) {
            System.err.println("ERROR al imprimir ticket de caja: " + e.getMessage());
            return ResponseEntity.status(500).body("❌ Error al imprimir: " + e.getMessage());
            // Aquí puedes registrar con Logger si prefieres
        }
    }

}
