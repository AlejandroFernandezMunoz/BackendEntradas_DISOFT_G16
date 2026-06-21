package edu.esi.ds.esientradas.services;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import edu.esi.ds.esientradas.model.DeZona;
import edu.esi.ds.esientradas.model.Entrada;
import edu.esi.ds.esientradas.model.Espectaculo;
import edu.esi.ds.esientradas.model.Precisa;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.util.List;

// Genera el PDF con las entradas compradas que se adjunta al correo de confirmacion.
@Slf4j
@Service
public class PDFService {

    public byte[] generarPdfEntradas(List<Entrada> entradas) {
        Document document = new Document(PageSize.A5);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            PdfWriter.getInstance(document, out);
            document.open();

            Font fuenteTitulo = new Font(Font.HELVETICA, 20, Font.BOLD, new Color(30, 58, 138));
            Font fuenteSubtitulo = new Font(Font.HELVETICA, 11, Font.NORMAL, new Color(107, 114, 128));
            Font fuenteCampo = new Font(Font.HELVETICA, 10, Font.BOLD, new Color(55, 65, 81));
            Font fuenteValor = new Font(Font.HELVETICA, 10, Font.NORMAL, new Color(17, 24, 39));
            Font fuenteId = new Font(Font.HELVETICA, 8, Font.ITALIC, new Color(156, 163, 175));

            Paragraph titulo = new Paragraph("EsiEntradas", fuenteTitulo);
            titulo.setAlignment(Element.ALIGN_CENTER);
            document.add(titulo);

            Paragraph subtitulo = new Paragraph("Ticket de entrada - Conserva este documento", fuenteSubtitulo);
            subtitulo.setAlignment(Element.ALIGN_CENTER);
            subtitulo.setSpacingAfter(20);
            document.add(subtitulo);

            for (Entrada entrada : entradas) {
                Espectaculo esp = entrada.getEspectaculo();

                PdfPTable tabla = new PdfPTable(2);
                tabla.setWidthPercentage(100);
                tabla.setWidths(new float[]{1f, 2f});
                tabla.setSpacingBefore(10);
                tabla.setSpacingAfter(10);

                PdfPCell cabecera = new PdfPCell(new Phrase(esp.getArtista(),
                        new Font(Font.HELVETICA, 13, Font.BOLD, Color.WHITE)));
                cabecera.setColspan(2);
                cabecera.setBackgroundColor(new Color(30, 58, 138));
                cabecera.setPadding(10);
                cabecera.setBorder(Rectangle.NO_BORDER);
                tabla.addCell(cabecera);

                agregarFila(tabla, "Fecha", esp.getFecha().toString(), fuenteCampo, fuenteValor);
                agregarFila(tabla, "Escenario", esp.getEscenario().getNombre(), fuenteCampo, fuenteValor);
                agregarFila(tabla, "Precio", String.format("%.2f EUR", entrada.getPrecio() / 100.0), fuenteCampo, fuenteValor);

                if (entrada instanceof DeZona zona) {
                    agregarFila(tabla, "Ubicacion", "Zona " + zona.getZona(), fuenteCampo, fuenteValor);
                } else if (entrada instanceof Precisa precisa) {
                    agregarFila(tabla, "Ubicacion",
                            "Planta " + precisa.getPlanta() + " - Fila " + precisa.getFila() + " - Butaca " + precisa.getColumna(),
                            fuenteCampo, fuenteValor);
                }

                PdfPCell celdaId = new PdfPCell(new Phrase("ID de entrada: " + entrada.getId(), fuenteId));
                celdaId.setColspan(2);
                celdaId.setBackgroundColor(new Color(249, 250, 251));
                celdaId.setPadding(6);
                celdaId.setBorder(Rectangle.NO_BORDER);
                tabla.addCell(celdaId);

                document.add(tabla);
            }

            document.close();
        } catch (DocumentException e) {
            log.error("Error al generar el PDF de entradas", e);
        }
        return out.toByteArray();
    }

    private void agregarFila(PdfPTable tabla, String campo, String valor, Font fCampo, Font fValor) {
        PdfPCell c1 = new PdfPCell(new Phrase(campo, fCampo));
        c1.setPadding(7);
        c1.setBackgroundColor(new Color(243, 244, 246));
        c1.setBorder(Rectangle.NO_BORDER);

        PdfPCell c2 = new PdfPCell(new Phrase(valor, fValor));
        c2.setPadding(7);
        c2.setBorder(Rectangle.NO_BORDER);

        tabla.addCell(c1);
        tabla.addCell(c2);
    }
}
