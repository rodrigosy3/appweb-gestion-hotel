package com.hotel.appHotel.service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.stereotype.Service;

import com.hotel.appHotel.model.Usuarios;
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
public class PdfServiceClientes {
    public byte[] generarPdfClientes(List<Usuarios> usuarios) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4.rotate());
            PdfWriter.getInstance(document, baos);
            document.open();

            // Agregar título
            Font fontTitulo = new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD);
            Paragraph titulo = new Paragraph("Lista de clientes", fontTitulo);
            titulo.setAlignment(Element.ALIGN_CENTER);
            document.add(titulo);

            document.add(new Paragraph("\n"));

            // Crear la tabla con 5 columnas
            PdfPTable table = new PdfPTable(8);
            table.setWidthPercentage(100);
            // table.setSpacingBefore(10f);
            // table.setSpacingAfter(10f);

            // Encabezados
            Font fontEncabezado = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
            String[] encabezados = { "ID", "D.N.I.", "Cliente", "Celular", "Edad", "Estado de veto", "Razón de veto",
                    "Fecha del registro" };
            float[] columnWidths = { 2f, 4f, 10f, 4f, 2f, 5f, 10f, 5f };
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
            for (Usuarios cliente : usuarios) {
                cell = new PdfPCell(new Phrase(String.valueOf(cliente.getId_usuario()), fontDatos));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(String.valueOf(cliente.getDni()), fontDatos));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(String.valueOf(cliente.getNombres() + ' '
                        + cliente.getApellidos()), fontDatos));
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(String.valueOf(cliente.getCelular()), fontDatos));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(String.valueOf(cliente.getEdad()), fontDatos));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);

                if (cliente.getEstado_vetado()) {
                    cell = new PdfPCell(new Phrase("VETADO", fontDatos));
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(cell);
                } else {
                    cell = new PdfPCell(new Phrase("NO VETADO", fontDatos));
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(cell);
                }

                cell = new PdfPCell(new Phrase(cliente.getRazon_vetado(), fontDatos));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);

                // cell = new PdfPCell(new Phrase(cliente.getFecha_creacion(), fontDatos));
                // cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                // table.addCell(cell);

                cell = new PdfPCell(new Phrase(formatearFecha(cliente.getFecha_creacion()), fontDatos));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
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
