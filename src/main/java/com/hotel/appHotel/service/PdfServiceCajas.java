package com.hotel.appHotel.service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.stereotype.Service;

import com.hotel.appHotel.model.Cajas;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

@Service
public class PdfServiceCajas {

    public byte[] generarPdfCajas(List<Cajas> cajas) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            // Crear documento en orientación horizontal
            Document document = new Document(PageSize.A4.rotate());
            PdfWriter.getInstance(document, baos);
            document.open();

            // ======================
            // 1. Título y Fecha
            // ======================
            Font fontTitulo = new Font(FontFamily.HELVETICA, 18, Font.BOLD);
            Paragraph titulo = new Paragraph("Reporte de Movimientos de Caja", fontTitulo);
            titulo.setAlignment(Element.ALIGN_CENTER);
            document.add(titulo);

            // Fecha de generación del reporte
            Font fontFecha = new Font(FontFamily.HELVETICA, 10, Font.ITALIC);
            String fechaActual = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm a"));
            Paragraph fechaParrafo = new Paragraph("Fecha de generación: " + fechaActual, fontFecha);
            fechaParrafo.setAlignment(Element.ALIGN_CENTER);
            document.add(fechaParrafo);

            document.add(new Paragraph("\n"));

            // ======================
            // 2. Tabla de datos (agregamos columna Tipo de Venta)
            // ======================
            PdfPTable table = new PdfPTable(6);
            table.setWidthPercentage(100);
            // Ajustamos anchos: ID, Venta ID, Habitación, Tipo Venta, Monto, Fecha Registro
            float[] columnWidths = {2f, 3f, 6f, 4f, 4f, 5f};
            table.setWidths(columnWidths);

            // Encabezados
            Font fontEncabezado = new Font(FontFamily.HELVETICA, 12, Font.BOLD);
            String[] encabezados = {
                "ID",
                "Venta ID",
                "Habitación",
                "Tipo de Venta",
                "Monto (S/.)",
                "Fecha Registro"
            };
            for (String encabezado : encabezados) {
                PdfPCell cell = new PdfPCell(new Phrase(encabezado, fontEncabezado));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                table.addCell(cell);
            }

            // Datos de la tabla
            Font fontDatos = new Font(FontFamily.HELVETICA, 10, Font.NORMAL);
            double totalSum = 0.0;

            for (Cajas caja : cajas) {
                // 1. Columna ID de movimiento
                PdfPCell cellId = new PdfPCell(new Phrase(String.valueOf(caja.getIdCaja()), fontDatos));
                cellId.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cellId);

                // 2. Columna Venta ID
                Long ventaId = caja.getVenta() != null ? caja.getVenta().getId_venta() : null;
                PdfPCell cellVentaId = new PdfPCell(
                        new Phrase(ventaId != null ? ventaId.toString() : "-", fontDatos)
                );
                cellVentaId.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cellVentaId);

                // 3. Columna Habitación (número y tipo)
                String habitacionTexto = "-";
                if (caja.getVenta() != null && caja.getVenta().getHabitacion() != null) {
                    int numeroHab = caja.getVenta().getHabitacion().getNumero();
                    String abreviacion = caja.getVenta().getHabitacion().getTipo().getAbreviacion_tipo();
                    String nombreTipo = caja.getVenta().getHabitacion().getTipo().getNombre_tipo();
                    habitacionTexto = String.format("[%d] (%s) %s", numeroHab, abreviacion, nombreTipo);
                }
                PdfPCell cellHabitacion = new PdfPCell(new Phrase(habitacionTexto, fontDatos));
                cellHabitacion.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cellHabitacion);

                // 4. Columna Tipo de Venta
                String tipoVentaTexto = caja.getVenta() != null ? caja.getVenta().getTipo_venta() : "-";
                PdfPCell cellTipoVenta = new PdfPCell(new Phrase(tipoVentaTexto, fontDatos));
                cellTipoVenta.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cellTipoVenta);

                // 5. Columna Monto
                PdfPCell cellMonto = new PdfPCell(
                        new Phrase(String.format("S/. %.2f", caja.getMonto()), fontDatos)
                );
                cellMonto.setHorizontalAlignment(Element.ALIGN_RIGHT);
                table.addCell(cellMonto);

                // 6. Columna Fecha Registro
                PdfPCell cellFecha = new PdfPCell(
                        new Phrase(formatearFecha(caja.getFechaRegistro()), fontDatos)
                );
                cellFecha.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cellFecha);

                totalSum += caja.getMonto();
            }

            // ======================
            // 3. Fila de total
            // ======================
            Font fontTotalEtiqueta = new Font(FontFamily.HELVETICA, 12, Font.BOLDITALIC);
            // "TOTAL" abarcará las cuatro primeras columnas (ID, Venta ID, Habitación, Tipo Venta)
            PdfPCell totalLabelCell = new PdfPCell(new Phrase("TOTAL", fontTotalEtiqueta));
            totalLabelCell.setColspan(4);
            totalLabelCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            totalLabelCell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            table.addCell(totalLabelCell);

            // Celda vacía para alinear
            PdfPCell emptyCell = new PdfPCell(new Phrase(""));
            emptyCell.setColspan(1);
            emptyCell.setBorder(PdfPCell.NO_BORDER);
            table.addCell(emptyCell);

            // Celda con el monto total
            PdfPCell totalValueCell = new PdfPCell(
                    new Phrase(String.format("S/. %.2f", totalSum), fontTotalEtiqueta)
            );
            totalValueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            totalValueCell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            table.addCell(totalValueCell);

            // ======================
            // 4. Agregar tabla y cerrar
            // ======================
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
