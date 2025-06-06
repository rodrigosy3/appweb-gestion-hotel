package com.hotel.appHotel.config;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.SimpleDoc;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;

import com.hotel.appHotel.model.Cajas;
import com.hotel.appHotel.model.Habitaciones;
import com.hotel.appHotel.model.Tickets;
import com.hotel.appHotel.model.Usuarios;
import com.hotel.appHotel.model.Ventas;
import com.hotel.appHotel.model.VentasClientesHabitacion;

/**
 * Utilidad para imprimir tickets ESC/POS en XP-E200L (80 mm, 48 chars por
 * línea), usando CP-1252 y centrado de texto.
 */
public class TicketPrinter {

    private static final String NOMBRE_HOTEL = "HOSTAL V";
    private static final String DIRECCION_HOTEL = "Chala - Arequipa";
    // private static String telefonoHotel = "Teléfono: (056) 123-4567";

    /**
     * Busca una impresora cuyo nombre contenga el texto dado (ignorando
     * mayúsculas/minúsculas).
     */
    public static PrintService encontrarImpresora(String printerName) {
        PrintService[] servicios = PrintServiceLookup.lookupPrintServices(null, null);
        for (PrintService servicio : servicios) {
            if (servicio.getName().toLowerCase().contains(printerName.toLowerCase())) {
                return servicio;
            }
        }
        return null;
    }

    /**
     * Envia un texto ya formateado a la impresora, incluyendo comandos ESC/POS.
     *
     * @param impresoraNombre Nombre de la impresora tal como figura en el
     *                        sistema.
     * @param cuerpoTexto     Texto completo del ticket, incluyendo cabecera, pie,
     *                        guiones, saltos, etc.
     * @throws Exception Si no se encuentra la impresora o hay error al
     *                   imprimir.
     */
    public static void imprimirTicket(String impresoraNombre, String cuerpoTexto) throws Exception {
        PrintService impresora = encontrarImpresora(impresoraNombre);
        if (impresora == null) {
            throw new RuntimeException("Impresora no encontrada: " + impresoraNombre);
        }

        // Comandos de inicialización ESC/POS
        byte[] initPrinter = new byte[] { 0x1B, 0x40 }; // ESC @
        byte[] setCodePage = new byte[] { 0x1B, 0x74, 0x10 }; // ESC t 16 = CP1252
        byte[] corte = new byte[] { 0x1D, 0x56, 0x41, 0x10 }; // GS V A 16 = corte total

        // Convertir el cuerpo a bytes en CP1252
        byte[] cuerpoBytes = cuerpoTexto.getBytes("CP1252");

        // Concatenar todo
        byte[] ticketCompleto = new byte[initPrinter.length + setCodePage.length + cuerpoBytes.length + corte.length];

        int pos = 0;
        System.arraycopy(initPrinter, 0, ticketCompleto, pos, initPrinter.length);
        pos += initPrinter.length;

        System.arraycopy(setCodePage, 0, ticketCompleto, pos, setCodePage.length);
        pos += setCodePage.length;

        System.arraycopy(cuerpoBytes, 0, ticketCompleto, pos, cuerpoBytes.length);
        pos += cuerpoBytes.length;

        System.arraycopy(corte, 0, ticketCompleto, pos, corte.length);

        // Enviar a impresora
        DocFlavor flavor = DocFlavor.BYTE_ARRAY.AUTOSENSE;
        Doc doc = new SimpleDoc(ticketCompleto, flavor, null);
        DocPrintJob printJob = impresora.createPrintJob();
        PrintRequestAttributeSet attrs = new HashPrintRequestAttributeSet();
        printJob.print(doc, attrs);
    }

    public static void imprimirTicketCajaDelDia(Usuarios encargado, List<Cajas> cajas, LocalDate fecha)
            throws Exception {
        double totalAntiguas = cajas.stream()
                .filter(c -> "ANTIGUO".equalsIgnoreCase(c.getVenta().getHabitacion().getCategoria()))
                .mapToDouble(Cajas::getMonto)
                .sum();

        double totalModernas = cajas.stream()
                .filter(c -> "MODERNO".equalsIgnoreCase(c.getVenta().getHabitacion().getCategoria()))
                .mapToDouble(Cajas::getMonto)
                .sum();

        double totalGeneral = totalAntiguas + totalModernas;

        StringBuilder sb = new StringBuilder();
        sb.append(centerText(NOMBRE_HOTEL)).append("\n");
        sb.append(centerText("REPORTE CAJA DEL DÍA")).append("\n");
        sb.append("=".repeat(47)).append("\n");

        if (encargado != null) {
            sb.append(String.format("%-13s: %s\n", "ENCARGADO", encargado.getNombres()));
            sb.append(String.format("%-13s: %s\n", "CARGO", encargado.getRol().getNombre()));
        }
        sb.append(String.format("%-12s: %s\n", "FECHA", fecha.toString()));
        sb.append("=".repeat(47)).append("\n\n");

        sb.append(centerText("DETALLE DE VENTAS")).append("\n");
        sb.append(String.format("%-18s: S/. %.2f\n", "Habitaciones Antiguas", totalAntiguas));
        sb.append(String.format("%-18s: S/. %.2f\n", "Habitaciones Modernas", totalModernas));
        sb.append("\n");
        sb.append(centerText(String.format("TOTAL DEL DÍA: S/. %.2f", totalGeneral))).append("\n");

        sb.append("*".repeat(47)).append("\n");
        sb.append(frameCenteredMessage("¡GRACIAS POR SU TRABAJO!", '*')).append("\n");
        sb.append("*".repeat(47)).append("\n\n");

        TicketPrinter.imprimirTicket("XP-E200L", sb.toString());
    }

    public static void imprimirTicketVentaRealizada(Ventas venta, Tickets ticket,
            Usuarios usuarioResponsable, List<VentasClientesHabitacion> listaClientes, boolean esNuevo) {

        try {
            StringBuilder sb = new StringBuilder();

            // ==== ENCABEZADO ====
            sb.append(centerText(NOMBRE_HOTEL));
            sb.append(centerText(DIRECCION_HOTEL)).append("\n");
            sb.append("=".repeat(47)).append("\n");

            // ==== DATOS DEL ENCARGADO Y TICKET ====
            sb.append(String.format("CÓDIGO       : TK-%07d\n", ticket.getNumeroTicket()));
            sb.append(String.format("%-13s: %s\n", "ENCARGADO", usuarioResponsable.getNombres()));
            sb.append(String.format("%-13s: %s\n", "CARGO", usuarioResponsable.getRol().getNombre()));
            sb.append(String.format("FECHA EMISIÓN: %s\n",
                    formatearFecha(ticket.getFechaEmision())));
            sb.append("=".repeat(47)).append("\n");
            sb.append("\n");

            // ==== DATOS DE LA VENTA ====
            Habitaciones hab = venta.getHabitacion();
            sb.append(String.format("%-13s: [%d] %s\n", "HABITACIÓN", hab.getNumero(), hab.getTipo().getNombre_tipo()));
            sb.append(String.format("%-13s: %s\n", "SERVICIO", venta.getTipo_servicio()));
            sb.append(String.format("%-13s: %s\n", "VENTA", venta.getTipo_venta()));
            sb.append("-".repeat(47)).append("\n");

            // ==== CLIENTES ====
            sb.append("\n");

            if (listaClientes.isEmpty()) {
                sb.append("# Sin clientes registrados para esta venta #\n");
            } else if (listaClientes.size() == 1) {
                Usuarios cliente = listaClientes.get(0).getUsuario_alojado();
                String nombreCompleto = cliente.getNombres() + " " + cliente.getApellidos();
                String celular = cliente.getCelular();

                sb.append("CLIENTE: ").append(nombreCompleto);
                if (celular != null && !celular.trim().isEmpty()) {
                    sb.append(" (").append(celular).append(")");
                }
                sb.append("\n");
            } else {
                sb.append("CLIENTES   :\n");
                for (VentasClientesHabitacion vc : listaClientes) {
                    String nombre = vc.getUsuario_alojado().getNombres() + " " + vc.getUsuario_alojado().getApellidos();
                    String celular = vc.getUsuario_alojado().getCelular();
                    sb.append("  • ").append(nombre);
                    if (celular != null && !celular.trim().isEmpty()) {
                        sb.append(" (").append(celular).append(")");
                    }
                    sb.append("\n");
                }
            }

            sb.append("-".repeat(47)).append("\n");

            // ==== MONTOS ====
            sb.append("\n");
            if (venta.getTiempo_estadia() == 1) {
                sb.append(String.format("%-13s: %d día\n", "ESTADÍA", venta.getTiempo_estadia()));
            } else {
                sb.append(String.format("%-13s: %d días\n", "ESTADÍA", venta.getTiempo_estadia()));
            }
            sb.append(String.format("%-13s: S/. %.2f\n", "PRECIO", venta.getMonto_total()));
            sb.append(String.format("%-13s: S/. %.2f\n", "DESCUENTO", venta.getDescuento()));

            // ==== SALDO PENDIENTE CON ESPACIADO EXTRA ====
            sb.append("\n");
            sb.append(centerText(String.format("TOTAL: S/. %.2f", venta.getMonto_total())));
            sb.append("\n");
            sb.append("-".repeat(47)).append("\n");

            // ==== FECHAS ====
            sb.append(String.format("FECHA DE ENTRADA : %s\n", formatearFecha(venta.getFecha_entrada())));
            sb.append(String.format("FECHA DE SALIDA  : %s\n", formatearFecha(venta.getFecha_salida())));
            sb.append("\n");

            // ==== PAGO CON Y VUELTO ====
            double vuelto = venta.getMonto_adelanto() - (venta.getMonto_total() - venta.getDescuento());
            sb.append(String.format("%-13s: S/. %.2f\n", "PAGO CON", venta.getMonto_adelanto()));
            if (vuelto >= 0) {
                sb.append(String.format("%-13s: S/. %.2f\n", "VUELTO", vuelto));
            } else {
                sb.append(String.format("%-13s: S/. %.2f\n", "POR COBRAR", (vuelto * -1)));
            }

            sb.append("\n");
            sb.append("*".repeat(47)).append("\n");
            sb.append(frameCenteredMessage("¡GRACIAS POR SU PREFERENCIA!", '*')).append("\n");
            sb.append("*".repeat(47)).append("\n\n");

            // ==== Imprimir ====
            String ticketTexto = sb.toString();
            TicketPrinter.imprimirTicket("XP-E200L", ticketTexto);
            if (esNuevo) TicketPrinter.imprimirTicket("XP-E200L", ticketTexto);
            // System.out.println("Imprimiendo ticket de venta:\n" + ticketTexto);

        } catch (Exception e) {
            System.out.println("ERROR al imprimir ticket de venta: " + e.getMessage());
        }
    }

    /**
     * Centra un texto dentro de un ancho de 48 caracteres.
     */
    public static String centerText(String text) {
        if (text == null) {
            text = "";
        }
        if (text.length() > 48) {
            return text.substring(0, 48) + "\n";
        }
        int padding = (48 - text.length()) / 2;
        return " ".repeat(Math.max(0, padding)) + text + "\n";
    }

    public static String frameCenteredMessage(String mensaje, char simbolo) {
        int ancho = 47;
        int contenidoLargo = mensaje.length() + 2; // Espacios a cada lado
        int padding = (ancho - contenidoLargo) / 2;

        StringBuilder sb = new StringBuilder();
        sb.append(String.valueOf(simbolo).repeat(padding));
        sb.append(" ").append(mensaje).append(" ");
        sb.append(String.valueOf(simbolo).repeat(ancho - sb.length())); // Relleno restante

        return sb.toString();
    }

    public static String formatearFecha(String isoDate) {
        try {
            DateTimeFormatter entrada = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
            DateTimeFormatter salida = DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm a");
            return LocalDateTime.parse(isoDate, entrada).format(salida);
        } catch (Exception e) {
            return isoDate;
        }
    }

}
