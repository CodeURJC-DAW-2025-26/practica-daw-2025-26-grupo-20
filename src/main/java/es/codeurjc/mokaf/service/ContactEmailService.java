package es.codeurjc.mokaf.service;

import es.codeurjc.mokaf.model.ContactRequest;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class ContactEmailService {

    private final JavaMailSender emailSender;

    @Value("${spring.mail.username:mokafcoffe@gmail.com}")
    private String supportEmail;

    public ContactEmailService(JavaMailSender emailSender) {
        this.emailSender = emailSender;
    }

    public void sendContactEmail(ContactRequest request) {
        try {
            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            // We send the email to ourselves (support team)
            helper.setTo(supportEmail);
            // We CC the client so they have a copy
            if (request.getEmail() != null && !request.getEmail().trim().isEmpty()) {
                helper.setCc(request.getEmail());
            }
            // The from address MUST be the authenticated user to avoid Gmail SMTP errors
            helper.setFrom(supportEmail);
            // The reply-to address is the customer's email
            helper.setReplyTo(request.getEmail());

            helper.setSubject("Nuevo mensaje de Contacto: " + request.getSubject());

            String text = "Has recibido una nueva solicitud de contacto desde la web.\n\n" +
                    "Detalles del cliente:\n" +
                    "Nombre: " + request.getFirstName() + " "
                    + (request.getLastName() != null ? request.getLastName() : "") + "\n" +
                    "Email: " + request.getEmail() + "\n" +
                    "Teléfono: "
                    + (request.getPhone() != null && !request.getPhone().isEmpty() ? request.getPhone() : "N/D") + "\n"
                    +
                    "Recibir Newsletter: " + (request.isNewsletter() ? "Sí" : "No") + "\n\n" +
                    "Mensaje:\n" +
                    "----------------------------------------------------\n" +
                    request.getMessage() + "\n" +
                    "----------------------------------------------------\n";

            helper.setText(text, false);

            emailSender.send(message);

        } catch (MessagingException e) {
            System.err.println("Error enviando correo de contacto: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
