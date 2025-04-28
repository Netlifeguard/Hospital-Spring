package com.nie.patient.tools;

import com.nie.patient.pojo.Orders;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class PdfUtil {
    public static void ExportPdf(HttpServletRequest request, HttpServletResponse response, Orders order) throws IOException {
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=order.pdf");

        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
                contentStream.newLineAtOffset(100, 700);
                contentStream.showText("Order Details");
                contentStream.newLineAtOffset(0, -20);
                contentStream.showText("Order ID: " + order.getOId());
                contentStream.newLineAtOffset(0, -20);
                contentStream.showText("Order Date: " + order.getdName());
                contentStream.newLineAtOffset(0, -20);
                contentStream.showText("Order Amount: " + order.getOCheck());
                // 添加更多订单信息
                contentStream.endText();
            }

            document.save(response.getOutputStream());
        }
    }
}
