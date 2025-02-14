package com.hotel.appHotel.service;

import java.io.ByteArrayOutputStream;
import java.util.List;

import org.springframework.stereotype.Service;

import com.hotel.appHotel.model.HistorialVetos;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.*;

@Service
public class PdfServiceHistorialVetos {
    public byte[] generarPdfHistorialVetos(List<HistorialVetos> historialVetos) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4.rotate());
            PdfWriter.getInstance(document, baos);
            document.open();

            // Agregar título
            Font fontTitulo = new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD);
            Paragraph titulo = new Paragraph("Reporte del Historial de Vetos", fontTitulo);
            titulo.setAlignment(Element.ALIGN_CENTER);
            document.add(titulo);

            document.add(new Paragraph("\n"));

            // Crear la tabla con 5 columnas
            PdfPTable table = new PdfPTable(5);
            table.setWidthPercentage(100);
            // table.setSpacingBefore(10f);
            // table.setSpacingAfter(10f);

            // Encabezados
            Font fontEncabezado = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
            String[] encabezados = { "ID", "Responsable", "Cliente Reportado", "Razón del reporte",
                    "Fecha del reporte" };
            float[] columnWidths = { 2f, 10f, 10f, 10f, 5f };
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
            for (HistorialVetos hv : historialVetos) {
                cell = new PdfPCell(new Phrase(String.valueOf(hv.getId_historial_veto()), fontDatos));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(hv.getUsuario_responsable().getNombres() + " "
                        + hv.getUsuario_responsable().getApellidos(), fontDatos));
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(String.valueOf(hv.getUsuario_vetado().getNombres() + ' '
                        + hv.getUsuario_vetado().getApellidos()), fontDatos));
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(hv.getRazon(), fontDatos));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(hv.getFecha_creacion(), fontDatos));
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
