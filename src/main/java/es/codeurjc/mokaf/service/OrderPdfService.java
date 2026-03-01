package es.codeurjc.mokaf.service;

import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import es.codeurjc.mokaf.model.Order;
import es.codeurjc.mokaf.model.OrderItem;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;

@Service
public class OrderPdfService {

    public byte[] generateOrderPdf(Order order) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document document = new Document();
            PdfWriter.getInstance(document, out);
            document.open();

            // Font definitions
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20);
            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
            Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 12);

            // Title
            Paragraph title = new Paragraph("Factura de Pedido - Mokaf", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);

            // Order Header
            document.add(new Paragraph("Pedido #: " + order.getId(), headerFont));
            document.add(new Paragraph(
                    "Cliente: " + order.getUser().getName() + " (" + order.getUser().getEmail() + ")", normalFont));
            document.add(new Paragraph("Fecha: " + order.getPaidAt(), normalFont));
            document.add(new Paragraph("Sucursal: " + order.getBranch().getName(), normalFont));
            document.add(new Paragraph(" "));

            // Items Table
            PdfPTable table = new PdfPTable(4);
            table.setWidthPercentage(100);
            table.setWidths(new float[] { 4f, 2f, 2f, 2f });

            // Table Headers
            PdfPCell cell = new PdfPCell(new Phrase("Producto", headerFont));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBackgroundColor(com.itextpdf.text.BaseColor.LIGHT_GRAY);
            cell.setPadding(5);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("Precio", headerFont));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBackgroundColor(com.itextpdf.text.BaseColor.LIGHT_GRAY);
            cell.setPadding(5);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("Cantidad", headerFont));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBackgroundColor(com.itextpdf.text.BaseColor.LIGHT_GRAY);
            cell.setPadding(5);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("Total", headerFont));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBackgroundColor(com.itextpdf.text.BaseColor.LIGHT_GRAY);
            cell.setPadding(5);
            table.addCell(cell);

            // Table Rows
            for (OrderItem item : order.getItems()) {
                table.addCell(item.getProduct().getName());
                table.addCell(item.getUnitPrice().toString() + "€");
                table.addCell(String.valueOf(item.getQuantity()));
                table.addCell(item.getLineTotal().toString() + "€");
            }

            document.add(table);
            document.add(new Paragraph(" "));

            // Totals
            Paragraph subtotal = new Paragraph("Subtotal: " + order.getSubtotalAmount() + "€", normalFont);
            subtotal.setAlignment(Element.ALIGN_RIGHT);
            document.add(subtotal);

            Paragraph total = new Paragraph("Total: " + order.getTotalAmount() + "€", headerFont);
            total.setAlignment(Element.ALIGN_RIGHT);
            document.add(total);

            document.close();
            return out.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            return new byte[0];
        }
    }
}
