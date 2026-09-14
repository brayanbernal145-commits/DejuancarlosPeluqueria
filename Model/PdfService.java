package com.DJuanCarlosPeluqueria.DJuanCarlosPeluqueria.Model;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Image;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

@Service
public class PdfService {

    private final Color doradoApp = new Color(228, 180, 50);   // #E4B432
    private final Color oscuroApp = new Color(25, 25, 25);     // #191919
    private final Color grisSuave = new Color(245, 245, 245);  // Filas alternadas
    private final NumberFormat formatoMoneda = NumberFormat.getCurrencyInstance(new Locale("es", "CO"));

    // =========================================================================
    // 1. REPORTE DE CITAS (Corregido con la estructura real de la BD)
    // =========================================================================
    public ByteArrayInputStream generarReporteCitas(List<Cita> citas) {
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            document.add(crearEncabezado("Reporte de Citas Programadas"));
            document.add(new Paragraph(" "));

            PdfPTable table = new PdfPTable(6);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{1.2f, 2f, 2f, 1.8f, 1.8f, 2f});

            Font fontHeader = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, doradoApp);
            String[] headers = {"ID Cita", "Fecha", "Hora Inicio", "Estado", "ID Usuario", "Total"};

            for (String headerTitle : headers) {
                PdfPCell header = new PdfPCell();
                header.setBackgroundColor(oscuroApp);
                header.setPadding(6);
                header.setPhrase(new Phrase(headerTitle, fontHeader));
                table.addCell(header);
            }

            Font fontData = FontFactory.getFont(FontFactory.HELVETICA, 8, Color.BLACK);
            boolean esPar = false;

            for (Cita cita : citas) {
                Color fondoFila = esPar ? grisSuave : Color.WHITE;

                String totalFormateado = cita.getTotalServicio() != null
                        ? formatoMoneda.format(cita.getTotalServicio())
                        : "$0";

                table.addCell(crearCelda(String.valueOf(cita.getIdCita()), fontData, fondoFila));
                table.addCell(crearCelda(cita.getFecha() != null ? cita.getFecha().toString() : "N/A", fontData, fondoFila));
                table.addCell(crearCelda(cita.getHoraInicio() != null ? cita.getHoraInicio().toString() : "N/A", fontData, fondoFila));
                table.addCell(crearCelda(cita.getEstado() != null ? cita.getEstado() : "N/A", fontData, fondoFila));
                table.addCell(crearCelda(cita.getIdUsuarioFK() != null ? String.valueOf(cita.getIdUsuarioFK()) : "N/A", fontData, fondoFila));
                table.addCell(crearCelda(totalFormateado, fontData, fondoFila));

                esPar = !esPar;
            }

            document.add(table);
            document.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return new ByteArrayInputStream(out.toByteArray());
    }

    // =========================================================================
    // 2. REPORTE DE SERVICIOS (Corregido)
    // =========================================================================
    public ByteArrayInputStream generarReporteServicios(List<Servicios> servicios) {
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            document.add(crearEncabezado("Reporte de Catálogo de Servicios"));
            document.add(new Paragraph(" "));

            PdfPTable table = new PdfPTable(5);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{1f, 3f, 4f, 2f, 2f});

            Font fontHeader = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, doradoApp);
            String[] headers = {"ID", "Servicio", "Descripción", "Precio Base", "Categoría"};

            for (String headerTitle : headers) {
                PdfPCell header = new PdfPCell();
                header.setBackgroundColor(oscuroApp);
                header.setPadding(6);
                header.setPhrase(new Phrase(headerTitle, fontHeader));
                table.addCell(header);
            }

            Font fontData = FontFactory.getFont(FontFactory.HELVETICA, 8, Color.BLACK);
            boolean esPar = false;

            for (Servicios s : servicios) {
                Color fondoFila = esPar ? grisSuave : Color.WHITE;

                String precio = s.getPrecioBase() != null ? formatoMoneda.format(s.getPrecioBase()) : "$0";

                table.addCell(crearCelda(String.valueOf(s.getIdServicio()), fontData, fondoFila));
                table.addCell(crearCelda(s.getNombreServicio() != null ? s.getNombreServicio() : "N/A", fontData, fondoFila));
                table.addCell(crearCelda(s.getDescripcionServicio() != null ? s.getDescripcionServicio() : "N/A", fontData, fondoFila));
                table.addCell(crearCelda(precio, fontData, fondoFila));
                table.addCell(crearCelda(s.getCategoria() != null ? s.getCategoria() : "N/A", fontData, fondoFila));

                esPar = !esPar;
            }

            document.add(table);
            document.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return new ByteArrayInputStream(out.toByteArray());
    }

    // =========================================================================
    // 3. REPORTE DE INSUMOS
    // =========================================================================
    public ByteArrayInputStream generarReporteInsumos(List<Insumo> insumos) {
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            document.add(crearEncabezado("Reporte de Inventario de Insumos"));
            document.add(new Paragraph(" "));

            PdfPTable table = new PdfPTable(6);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{1f, 3f, 2.5f, 1.5f, 1.5f, 2f});

            Font fontHeader = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, doradoApp);
            String[] headers = {"ID", "Insumo", "Categoría", "Stock Actual", "Stock Mín.", "F. Vencimiento"};

            for (String headerTitle : headers) {
                PdfPCell header = new PdfPCell();
                header.setBackgroundColor(oscuroApp);
                header.setPadding(6);
                header.setPhrase(new Phrase(headerTitle, fontHeader));
                table.addCell(header);
            }

            Font fontData = FontFactory.getFont(FontFactory.HELVETICA, 8, Color.BLACK);
            Font fontAlerta = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8, Color.RED);
            boolean esPar = false;

            for (Insumo i : insumos) {
                Color fondoFila = esPar ? grisSuave : Color.WHITE;

                Font fuenteStock = (i.getStockActual() <= i.getStockMinPosible()) ? fontAlerta : fontData;

                table.addCell(crearCelda(String.valueOf(i.getIdInsumo()), fontData, fondoFila));
                table.addCell(crearCelda(i.getNombreInsumo() != null ? i.getNombreInsumo() : "N/A", fontData, fondoFila));
                table.addCell(crearCelda(i.getCategoriaInsumo() != null ? i.getCategoriaInsumo() : "N/A", fontData, fondoFila));
                table.addCell(crearCelda(String.valueOf(i.getStockActual()), fuenteStock, fondoFila));
                table.addCell(crearCelda(String.valueOf(i.getStockMinPosible()), fontData, fondoFila));
                table.addCell(crearCelda(i.getFechaVencimiento() != null ? i.getFechaVencimiento().toString() : "N/A", fontData, fondoFila));

                esPar = !esPar;
            }

            document.add(table);
            document.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return new ByteArrayInputStream(out.toByteArray());
    }

    // =========================================================================
    // 4. REPORTE DE USUARIOS
    // =========================================================================
    public ByteArrayInputStream generarReporteUsuarios(List<Usuario> usuarios) {
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            document.add(crearEncabezado("Reporte de Usuarios Registrados"));
            document.add(new Paragraph(" "));

            PdfPTable table = new PdfPTable(7);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{1f, 1.2f, 2f, 3f, 2f, 1.8f, 1.2f});

            Font fontHeader = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, doradoApp);
            String[] headers = {"ID", "Documento", "Nombre Completo", "Email", "Teléfono", "Cargo", "Estado"};

            for (String headerTitle : headers) {
                PdfPCell header = new PdfPCell();
                header.setBackgroundColor(oscuroApp);
                header.setPadding(6);
                header.setPhrase(new Phrase(headerTitle, fontHeader));
                table.addCell(header);
            }

            Font fontData = FontFactory.getFont(FontFactory.HELVETICA, 8, Color.BLACK);
            boolean esPar = false;

            for (Usuario u : usuarios) {
                Color fondoFila = esPar ? grisSuave : Color.WHITE;

                String doc = (u.getTipoDocumento() != null ? u.getTipoDocumento() : "") + " " + (u.getNumDocumento() != null ? u.getNumDocumento() : "");
                table.addCell(crearCelda(String.valueOf(u.getIdUsuario()), fontData, fondoFila));
                table.addCell(crearCelda(doc, fontData, fondoFila));
                table.addCell(crearCelda(u.getNombreCompleto() != null ? u.getNombreCompleto() : "N/A", fontData, fondoFila));
                table.addCell(crearCelda(u.getEmail() != null ? u.getEmail() : "N/A", fontData, fondoFila));
                table.addCell(crearCelda(u.getTelefono() != null ? String.valueOf(u.getTelefono()) : "N/A", fontData, fondoFila));
                table.addCell(crearCelda(u.getCargo() != null ? u.getCargo() : "N/A", fontData, fondoFila));
                table.addCell(crearCelda(u.getEstado() != null ? u.getEstado() : "N/A", fontData, fondoFila));

                esPar = !esPar;
            }

            document.add(table);
            document.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return new ByteArrayInputStream(out.toByteArray());
    }

    // =========================================================================
    // MÉTODOS AUXILIARES Y ENCABEZADO REUTILIZABLE
    // =========================================================================
    private PdfPTable crearEncabezado(String tituloReporte) {
        PdfPTable headerTable = new PdfPTable(2);
        headerTable.setWidthPercentage(100);
        try {
            headerTable.setWidths(new float[]{1f, 4f});

            ClassPathResource imgResource = new ClassPathResource("static/bostraap/icons/LOGO.jpeg");

            if (imgResource.exists()) {
                try (InputStream inputStream = imgResource.getInputStream()) {
                    byte[] bytes = inputStream.readAllBytes();
                    Image logo = Image.getInstance(bytes);
                    logo.scaleToFit(65, 65);

                    PdfPCell logoCell = new PdfPCell(logo);
                    logoCell.setBorder(PdfPCell.NO_BORDER);
                    logoCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    logoCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    headerTable.addCell(logoCell);
                }
            } else {
                PdfPCell emptyCell = new PdfPCell();
                emptyCell.setBorder(PdfPCell.NO_BORDER);
                headerTable.addCell(emptyCell);
            }

            Font fontTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, doradoApp);
            Paragraph titulo = new Paragraph(tituloReporte + "\nD'Juan Carlos Peluquería", fontTitulo);

            PdfPCell titleCell = new PdfPCell(titulo);
            titleCell.setBorder(PdfPCell.NO_BORDER);
            titleCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            headerTable.addCell(titleCell);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return headerTable;
    }

    private PdfPCell crearCelda(String texto, Font fuente, Color fondo) {
        PdfPCell cell = new PdfPCell(new Phrase(texto, fuente));
        cell.setBackgroundColor(fondo);
        cell.setPadding(5);
        return cell;
    }
}