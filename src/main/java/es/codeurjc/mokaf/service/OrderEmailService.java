package es.codeurjc.mokaf.service;

import es.codeurjc.mokaf.model.Order;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class OrderEmailService {

    private final JavaMailSender emailSender;
    private final OrderPdfService orderPdfService;

    @Value("${spring.mail.username:mokafcoffe@gmail.com}")
    private String fromEmail;

    public OrderEmailService(JavaMailSender emailSender, OrderPdfService orderPdfService) {
        this.emailSender = emailSender;
        this.orderPdfService = orderPdfService;
    }

    public void sendOrderConfirmationWithPdf(Order order) {
        try {
            // Generar PDF
            byte[] pdfBytes = orderPdfService.generateOrderPdf(order);

            // Crear email
            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom(fromEmail);
            helper.setTo(order.getUser().getEmail());
            // En copia a nosotros
            helper.setCc(fromEmail);
            helper.setSubject("Confirmación de Pedido - Mokaf #" + order.getId());

            String text = "Hola " + order.getUser().getName() + ",\n\n" +
                    "Gracias por tu compra en Mokaf. Adjuntamos en este correo la factura de tu pedido #" +
                    order.getId() + ".\n\n" +
                    "Subtotal: " + order.getSubtotalAmount() + "€\n" +
                    "Total pagado: " + order.getTotalAmount() + "€\n\n" +
                    "¡Esperamos volver a verte pronto!\n" +
                    "El equipo de Mokaf";

            helper.setText(text, false);

            // Adjuntar el PDF
            helper.addAttachment("Factura_Mokaf_" + order.getId() + ".pdf", new ByteArrayResource(pdfBytes));

            // Enviar email
            emailSender.send(message);
        } catch (MessagingException e) {
            System.err.println("Error enviando el email de la orden: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
