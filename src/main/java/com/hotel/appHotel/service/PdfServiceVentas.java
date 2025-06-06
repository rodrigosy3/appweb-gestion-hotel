package com.hotel.appHotel.service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.stereotype.Service;

import com.hotel.appHotel.model.Cajas;
import com.hotel.appHotel.model.Ventas;
import com.hotel.appHotel.model.VentasClientesHabitacion;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

@Service
public class PdfServiceVentas {

    public byte[] generarPdfVentas(List<Ventas> ventas) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4.rotate());
            PdfWriter.getInstance(document, baos);
            document.open();

            // Agregar título
            Font fontTitulo = new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD);
            Paragraph titulo = new Paragraph("Reporte de Ventas", fontTitulo);
            titulo.setAlignment(Element.ALIGN_CENTER);
            document.add(titulo);

            document.add(new Paragraph("\n"));

            // Crear la tabla con 5 columnas
            PdfPTable table = new PdfPTable(14);
            table.setWidthPercentage(100);
            // table.setSpacingBefore(10f);
            // table.setSpacingAfter(10f);

            // Encabezados
            Font fontEncabezado = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD);
            String[] encabezados = {"ID", "Hab.", "Cliente", "Precio habitación", "Días alojado",
                "Tipo de servicio",
                "Descuento", "Monto Adelanto", "Monto total", "Tipo de venta", "Fecha Entrada", "Fecha Salida",
                "Estado", "Responsable"};
            float[] columnWidths = {2f, 3f, 10f, 5f, 4f, 6f, 4f, 5f, 5f, 6f, 8f, 8f, 5f, 5f};
            table.setWidths(columnWidths);

            for (String encabezado : encabezados) {
                PdfPCell cell = new PdfPCell(new Phrase(encabezado, fontEncabezado));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                table.addCell(cell);
            }

            Font fontDatos = new Font(Font.FontFamily.HELVETICA, 8, Font.NORMAL);
            PdfPCell cell;
            // Datos de la tabla
            for (Ventas venta : ventas) {
                cell = new PdfPCell(new Phrase(String.valueOf(venta.getId_venta()), fontDatos));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(String.valueOf(venta.getHabitacion().getNumero()), fontDatos));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);

                StringBuilder clientesTexto = new StringBuilder();
                for (VentasClientesHabitacion cliente : venta.getVentasClientesHabitacion()) {
                    clientesTexto.append(cliente.getUsuario_alojado().getNombres())
                            .append(" ")
                            .append(cliente.getUsuario_alojado().getApellidos())
                            .append("\n");
                }

                cell = new PdfPCell(new Phrase(clientesTexto.toString().trim(), fontDatos));
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(String.valueOf("S/. " + venta.getHabitacion().getPrecio()), fontDatos));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(String.valueOf(venta.getTiempo_estadia()), fontDatos));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(venta.getTipo_servicio(), fontDatos));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);

                if (venta.getDescuento() == null) {
                    cell = new PdfPCell(new Phrase("S/. 0.00", fontDatos));
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(cell); // O algún valor por defecto
                } else {
                    cell = new PdfPCell(new Phrase(String.valueOf("S/. " + venta.getDescuento()), fontDatos));
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(cell);
                }

                if (venta.getMonto_adelanto() == null) {
                    cell = new PdfPCell(new Phrase("S/. 0.00", fontDatos));
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(cell); // O algún valor por defecto
                } else {
                    cell = new PdfPCell(new Phrase(String.valueOf("S/. " + venta.getMonto_adelanto()), fontDatos));
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(cell);
                }

                cell = new PdfPCell(new Phrase(String.valueOf("S/. " + venta.getMonto_total()), fontDatos));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(venta.getTipo_venta(), fontDatos));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);

                // cell = new PdfPCell(new Phrase(venta.getFecha_entrada(), fontDatos));
                // table.addCell(cell);
                // cell = new PdfPCell(new Phrase(venta.getFecha_salida(), fontDatos));
                // table.addCell(cell);
                cell = new PdfPCell(new Phrase(formatearFecha(venta.getFecha_entrada()), fontDatos));
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(formatearFecha(venta.getFecha_salida()), fontDatos));
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(venta.getEstado(), fontDatos));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(venta.getUsuario_responsable().getNombres() + " "
                        + venta.getUsuario_responsable().getApellidos(), fontDatos));
                table.addCell(cell);
            }

            document.add(table);
            document.close();

            return baos.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public byte[] generarPdfVentasConCaja(List<Ventas> ventas, List<Cajas> cajas) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4.rotate());
            PdfWriter.getInstance(document, baos);
            document.open();

            // ===== TÍTULO PRINCIPAL =====
            Font fontTitulo = new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD);
            Paragraph titulo = new Paragraph("Reporte de Ventas", fontTitulo);
            titulo.setAlignment(Element.ALIGN_CENTER);
            document.add(titulo);

            document.add(new Paragraph("\n"));

            // ===== RESUMEN CAJA POR CATEGORÍA =====
            Font fontResumen = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
            Paragraph resumen = new Paragraph("Resumen por Categoría", fontResumen);
            document.add(resumen);

            double totalAntiguas = cajas.stream()
                    .filter(c -> c.getVenta().getHabitacion().getCategoria().equalsIgnoreCase("ANTIGUO"))
                    .mapToDouble(Cajas::getMonto).sum();

            double totalModernas = cajas.stream()
                    .filter(c -> c.getVenta().getHabitacion().getCategoria().equalsIgnoreCase("MODERNO"))
                    .mapToDouble(Cajas::getMonto).sum();

            double totalGeneral = totalAntiguas + totalModernas;

            Font fontDatosResumen = new Font(Font.FontFamily.HELVETICA, 10);
            document.add(new Paragraph(String.format("Total Habitaciones Antiguas: S/. %.2f", totalAntiguas), fontDatosResumen));
            document.add(new Paragraph(String.format("Total Habitaciones Modernas: S/. %.2f", totalModernas), fontDatosResumen));
            document.add(new Paragraph(String.format("TOTAL GENERAL: S/. %.2f", totalGeneral), fontDatosResumen));

            document.add(new Paragraph("\n"));

            // ===== TABLA DE VENTAS =====
            PdfPTable table = new PdfPTable(14);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{2f, 3f, 10f, 5f, 4f, 6f, 4f, 5f, 5f, 6f, 8f, 8f, 5f, 5f});

            Font fontEncabezado = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD);
            String[] encabezados = {"ID", "Hab.", "Cliente", "Precio habitación", "Días alojado",
                "Tipo de servicio", "Descuento", "Monto Adelanto", "Monto total", "Tipo de venta",
                "Fecha Entrada", "Fecha Salida", "Estado", "Responsable"};

            for (String encabezado : encabezados) {
                PdfPCell cell = new PdfPCell(new Phrase(encabezado, fontEncabezado));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                table.addCell(cell);
            }

            Font fontDatos = new Font(Font.FontFamily.HELVETICA, 8);
            for (Ventas venta : ventas) {
                table.addCell(new PdfPCell(new Phrase(String.valueOf(venta.getId_venta()), fontDatos)));
                table.addCell(new PdfPCell(new Phrase(String.valueOf(venta.getHabitacion().getNumero()), fontDatos)));

                StringBuilder clientesTexto = new StringBuilder();
                for (VentasClientesHabitacion cliente : venta.getVentasClientesHabitacion()) {
                    clientesTexto.append(cliente.getUsuario_alojado().getNombres())
                            .append(" ").append(cliente.getUsuario_alojado().getApellidos())
                            .append("\n");
                }
                table.addCell(new PdfPCell(new Phrase(clientesTexto.toString().trim(), fontDatos)));

                table.addCell(new PdfPCell(new Phrase("S/. " + venta.getHabitacion().getPrecio(), fontDatos)));
                table.addCell(new PdfPCell(new Phrase(String.valueOf(venta.getTiempo_estadia()), fontDatos)));
                table.addCell(new PdfPCell(new Phrase(venta.getTipo_servicio(), fontDatos)));
                table.addCell(new PdfPCell(new Phrase("S/. " + (venta.getDescuento() == null ? "0.00" : venta.getDescuento()), fontDatos)));
                table.addCell(new PdfPCell(new Phrase("S/. " + (venta.getMonto_adelanto() == null ? "0.00" : venta.getMonto_adelanto()), fontDatos)));
                table.addCell(new PdfPCell(new Phrase("S/. " + venta.getMonto_total(), fontDatos)));
                table.addCell(new PdfPCell(new Phrase(venta.getTipo_venta(), fontDatos)));
                table.addCell(new PdfPCell(new Phrase(formatearFecha(venta.getFecha_entrada()), fontDatos)));
                table.addCell(new PdfPCell(new Phrase(formatearFecha(venta.getFecha_salida()), fontDatos)));
                table.addCell(new PdfPCell(new Phrase(venta.getEstado(), fontDatos)));
                table.addCell(new PdfPCell(new Phrase(venta.getUsuario_responsable().getNombres() + " " + venta.getUsuario_responsable().getApellidos(), fontDatos)));
            }

            document.add(table);
            document.close();
            return baos.toByteArray();
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }

    private String formatearFecha(String fechaIso) {
        try {
            DateTimeFormatter inputFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
            DateTimeFormatter outputFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm a");
            return LocalDateTime.parse(fechaIso, inputFormat).format(outputFormat);
        } catch (Exception e) {
            return fechaIso; // Devuelve sin formato si falla el parseo
        }
    }

}
