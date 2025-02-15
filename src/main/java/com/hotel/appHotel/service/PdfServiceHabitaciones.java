package com.hotel.appHotel.service;

import com.hotel.appHotel.model.Habitaciones;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.List;

@Service
public class PdfServiceHabitaciones {

    public byte[] generarPdfHabitaciones(List<Habitaciones> habitaciones) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4.rotate());
            PdfWriter.getInstance(document, baos);
            document.open();

            // Agregar título
            Font fontTitulo = new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD);
            Paragraph titulo = new Paragraph("Reporte de habitaciones", fontTitulo);
            titulo.setAlignment(Element.ALIGN_CENTER);
            document.add(titulo);

            document.add(new Paragraph("\n"));

            // Crear la tabla con 5 columnas
            PdfPTable table = new PdfPTable(8);
            table.setWidthPercentage(100);
            // table.setSpacingBefore(10f);
            // table.setSpacingAfter(10f);

            // Encabezados
            Font fontEncabezado = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD);
            String[] encabezados = { "ID", "N°", "Tipo", "Categoria", "Capacidad",
                    "Precio", "Estado", "Razón de estado" };
            float[] columnWidths = { 2f, 3f, 10f, 4f, 2f, 3f, 4f, 10f };
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
            for (Habitaciones hab : habitaciones) {
                cell = new PdfPCell(new Phrase(String.valueOf(hab.getId_habitacion()), fontDatos));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(String.valueOf(hab.getNumero()), fontDatos));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase("[" + hab.getTipo().getAbreviacion_tipo() + "] "
                        + hab.getTipo().getNombre_tipo(), fontDatos));
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(String.valueOf(hab.getCategoria()), fontDatos));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(String.valueOf(hab.getCapacidad()), fontDatos));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);

                if (hab.getPrecio() == null) {
                    cell = new PdfPCell(new Phrase("S/. 0.00", fontDatos));
                } else {
                    cell = new PdfPCell(new Phrase(String.valueOf("S/. " + hab.getPrecio()), fontDatos));
                }
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(hab.getEstado().getEstado(), fontDatos));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(hab.getRazon_estado(), fontDatos));
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
