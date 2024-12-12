package pe.edu.utp.controller.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Map;

@RestController
public class PdfController {

    private static final Logger logger = LoggerFactory.getLogger(PdfController.class);

    @PostMapping("/download/pdf")
    public ResponseEntity<byte[]> downloadPdf(@RequestBody Map<String, List<List<String>>> data) {
        // Usar un ByteArrayOutputStream para el PDF
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            // Crear PdfWriter y PdfDocument
            try (PdfWriter writer = new PdfWriter(byteArrayOutputStream);
                 PdfDocument pdfDocument = new PdfDocument(writer);
                 Document document = new Document(pdfDocument)) {

                // Obtener los datos de la tabla desde la solicitud
                List<List<String>> tableData = data.get("tableData");

                if (tableData == null || tableData.isEmpty()) {
                    return ResponseEntity.badRequest().body("No data to generate PDF".getBytes());
                }

                // Crear una tabla con el número de columnas basado en la primera fila
                Table table = new Table(tableData.get(0).size());

                // Agregar los encabezados (primer fila de la tabla)
                for (String header : tableData.get(0)) {
                    table.addHeaderCell(new Cell().add(new Paragraph(header)));
                }

                // Agregar los datos (filas restantes de la tabla)
                for (int i = 1; i < tableData.size(); i++) {
                    List<String> rowData = tableData.get(i);
                    for (String cellData : rowData) {
                        table.addCell(new Cell().add(new Paragraph(cellData)));
                    }
                }

                document.add(table); // Agregar la tabla al documento

            } // pdfDocument y writer se cierran automáticamente aquí

            // Configurar los encabezados para la descarga
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "attachment; filename=tabla.pdf");
            headers.add("Content-Type", "application/pdf");

            return new ResponseEntity<>(byteArrayOutputStream.toByteArray(), headers, HttpStatus.OK);

        } catch (Exception e) {
            e.printStackTrace();
            logger.error("ERROR: El PDF no se descargo correctamente", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
