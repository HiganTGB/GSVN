package com.gsvn.inventoryservice.service;

import com.gsvn.inventoryservice.model.dto.response.InboundResponse;

import com.gsvn.inventoryservice.model.dto.response.OutboundResponse;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.io.ByteArrayOutputStream;

@Service
@RequiredArgsConstructor
public class PdfExportService {

    private final SpringTemplateEngine templateEngine;

    public byte[] exportInboundPdf(InboundResponse data, String supplierName, String staffName, String warehouseName) {
        try (ByteArrayOutputStream os = new ByteArrayOutputStream();
             java.io.InputStream fontStream = new ClassPathResource("/fonts/times.ttf").getInputStream()) {

            Context context = new Context();
            context.setVariable("receipt", data);
            context.setVariable("warehouseName", warehouseName);
            context.setVariable("supplierName", supplierName);
            context.setVariable("staffName", staffName);
            String htmlContent = templateEngine.process("pdf/inbound", context);

            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.useFastMode();

            builder.useFont(() -> fontStream, "Times New Roman");

            builder.withHtmlContent(htmlContent, "/");
            builder.toStream(os);
            builder.run();

            return os.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Lỗi render PDF: " + e.getMessage());
        }
    }
    public byte[] exportOutboundPdf(OutboundResponse data, String staffName, String warehouseName) {
        try (ByteArrayOutputStream os = new ByteArrayOutputStream();
             java.io.InputStream fontStream = new ClassPathResource("/fonts/times.ttf").getInputStream()) {
            Context context = new Context();
            context.setVariable("receipt", data);
            context.setVariable("warehouseName", warehouseName);
            context.setVariable("staffName", staffName);
            String htmlContent = templateEngine.process("pdf/inbound", context);

            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.useFastMode();

            builder.useFont(() -> fontStream, "Times New Roman");

            builder.withHtmlContent(htmlContent, "/");
            builder.toStream(os);
            builder.run();

            return os.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Lỗi render PDF: " + e.getMessage());
        }
    }
}