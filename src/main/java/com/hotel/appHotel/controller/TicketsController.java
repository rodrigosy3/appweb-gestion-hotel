package com.hotel.appHotel.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hotel.appHotel.config.TicketPrinter;
import com.hotel.appHotel.model.Cajas;
import com.hotel.appHotel.model.Tickets;
import com.hotel.appHotel.model.Usuarios;
import com.hotel.appHotel.model.Ventas;
import com.hotel.appHotel.model.VentasClientesHabitacion;
import com.hotel.appHotel.repository.CajasRepository;
import com.hotel.appHotel.repository.CredencialesRepository;
import com.hotel.appHotel.repository.VentasClientesHabitacionRepository;
import com.hotel.appHotel.service.TicketsService;
import com.hotel.appHotel.service.VentasService;

@Controller
public class TicketsController {

    @Autowired
    private CredencialesRepository repositorioCredenciales;

    @Autowired
    private CajasRepository repositorioCajas;

    @Autowired
    private VentasClientesHabitacionRepository repositorioVentasClientesHabitacion;

    @Autowired
    private VentasService servicioVentas;

    @Autowired
    private TicketsService servicioTickets;

    @GetMapping("/ticket-caja-por-fecha")
    @ResponseBody
    public ResponseEntity<String> imprimirTicketCajaPorFecha(
            @RequestParam(value = "fecha", required = false) String fecha) {
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
            List<Cajas> cajasDelDia = repositorioCajas.obtenerCajasDelDia(String.valueOf(fechaFiltro));

            // Imprimir el ticket con la lógica centralizada
            TicketPrinter.imprimirTicketCajaDelDia(usuario, cajasDelDia, fechaFiltro);

            return ResponseEntity.ok("✅ Ticket impreso correctamente");

        } catch (Exception e) {
            System.err.println("ERROR al imprimir ticket de caja: " + e.getMessage());
            return ResponseEntity.status(500).body("❌ Error al imprimir: " + e.getMessage());
            // Aquí puedes registrar con Logger si prefieres
        }
    }

    @GetMapping("/imprimir-ticket-copia")
    @ResponseBody
    public String imprimirTicketCopiaVenta(@RequestParam("idVenta") Long idVenta) {
        try {
            // Buscar la venta
            Ventas venta = servicioVentas.getVentaById(idVenta);
            if (venta == null) {
                return "❌ Venta no encontrada.";
            }
            // Verificar si la venta está pagada (opcional si ya controlas esto por ticket)
            if (!"PAGADO".equalsIgnoreCase(venta.getEstado())) {
                return "❌ No hay ticket: La habitación aún no ha sido pagada.";
            }

            // Obtener usuario logueado (si aplica)
            String dni = SecurityContextHolder.getContext().getAuthentication().getName();
            Usuarios encargado = repositorioCredenciales.buscarPorDni(dni).map(c -> c.getUsuario()).orElse(null);

            // Buscar último ticket asociado a esa venta
            Tickets ticketOpt = servicioTickets.getUltimoByVentaId(idVenta);
            if (ticketOpt == null) {
                return "❌ No existe ticket asociado a esta venta.";
            }

            Tickets ticket = ticketOpt;

            // Obtener los clientes
            List<VentasClientesHabitacion> listaClientes = repositorioVentasClientesHabitacion
                    .findByVentaId(venta.getId_venta());

            // Imprimir el ticket
            TicketPrinter.imprimirTicketVentaRealizada(venta, ticket, encargado, listaClientes, false);

            return "✅ Copia de ticket impresa correctamente.";

        } catch (Exception e) {
            System.err.println("Error al imprimir ticket de copia: " + e.getMessage());
            return "❌ Error inesperado al imprimir ticket.";
        }
    }

}
