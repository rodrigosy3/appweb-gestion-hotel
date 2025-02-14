package com.hotel.appHotel.service;

import com.hotel.appHotel.model.Ventas;
import com.hotel.appHotel.model.VentasClientesHabitacion;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.List;

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
            String[] encabezados = { "ID", "Hab.", "Cliente", "Precio habitación", "Días alojado",
                    "Tipo de servicio",
                    "Descuento", "Monto Adelanto", "Monto total", "Tipo de venta", "Fecha Entrada", "Fecha Salida",
                    "Estado", "Responsable" };
            float[] columnWidths = { 2f, 3f, 10f, 5f, 4f, 6f, 4f, 5f, 5f, 6f, 8f, 8f, 5f, 5f };
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

                for (VentasClientesHabitacion cliente : venta.getVentasClientesHabitacion()) {
                    cell = new PdfPCell(new Phrase(String.valueOf(cliente.getUsuario_alojado().getNombres() + ' '
                            + cliente.getUsuario_alojado().getApellidos()), fontDatos));
                    table.addCell(cell);
                }

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

                cell = new PdfPCell(new Phrase(venta.getFecha_entrada(), fontDatos));
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(venta.getFecha_salida(), fontDatos));
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

}
