package edu.esi.ds.esientradas.services;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

// Envia al comprador el correo de confirmacion con el PDF de las entradas adjunto.
// Transporte: Brevo (SMTP relay), configurado en application.properties.
@Slf4j
@Service
public class EmailService {

    private final JavaMailSender emailSender;

    @Value("${app.mail.sender}")
    private String remitente;

    public EmailService(JavaMailSender emailSender) {
        this.emailSender = emailSender;
    }

    public void enviarEntradasConAdjunto(String destinatario, byte[] pdfBytes) {
        try {
            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(remitente);
            helper.setTo(destinatario);
            helper.setSubject("Tus entradas para el espectaculo - EsiEntradas");

            String html = """
                <div style="font-family: Arial, sans-serif; max-width: 600px; margin: auto; border: 1px solid #e5e7eb; border-radius: 12px; overflow: hidden;">
                  <div style="background-color: #1e3a8a; padding: 24px; text-align: center;">
                    <h1 style="color: white; margin: 0; font-size: 1.8rem;">EsiEntradas</h1>
                    <p style="color: #93c5fd; margin: 8px 0 0 0;">Tu compra ha sido confirmada</p>
                  </div>
                  <div style="padding: 32px 24px;">
                    <h2 style="color: #111827; margin-top: 0;">Gracias por tu compra</h2>
                    <p style="color: #4b5563; line-height: 1.6;">
                      Adjunto encontraras el <strong>PDF con tus entradas</strong>. Presentalo en la puerta el dia del evento.
                    </p>
                  </div>
                  <div style="background-color: #f9fafb; padding: 16px 24px; text-align: center; border-top: 1px solid #e5e7eb;">
                    <p style="color: #9ca3af; font-size: 0.8rem; margin: 0;">EsiEntradas</p>
                  </div>
                </div>
            """;

            helper.setText(html, true);
            helper.addAttachment("mis-entradas.pdf", new ByteArrayResource(pdfBytes));
            emailSender.send(message);
            log.info("Correo con entradas enviado a {}", destinatario);
        } catch (MessagingException e) {
            log.error("Error al enviar el correo con las entradas", e);
        }
    }
}
