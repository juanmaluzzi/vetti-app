package org.vetti.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.vetti.model.request.ScheduleRequest;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${email.to}")
    private String emailTo;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendScheduleEmail(ScheduleRequest scheduleRequest) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setTo(emailTo);
        helper.setSubject("BACKOFFICE - Nuevo horario recibido para " + scheduleRequest.getVetName());
        helper.setText(buildEmailBody(scheduleRequest), true);

        mailSender.send(message);
        System.out.println("Correo enviado a " + emailTo);
    }

    private String buildEmailBody(ScheduleRequest scheduleRequest) {
        StringBuilder body = new StringBuilder();

        body.append("<h2>Detalles de la Veterinaria:</h2>");
        body.append("<p><strong>Nombre:</strong> ").append(scheduleRequest.getVetName()).append("</p>");
        body.append("<p><strong>Email:</strong> ").append(scheduleRequest.getVetEmail()).append("</p>");
        body.append("<p><strong>Servicio:</strong> ").append(scheduleRequest.getService()).append("</p>");

        body.append("<h3>Se ha recibido el/los siguiente/s horario:</h3>");
        scheduleRequest.getDays().forEach(day -> {
            body.append("<p><strong>Día:</strong> ").append(day.getDay()).append("</p>");
            body.append("<ul>");
            day.getTimeSlots().forEach(slot -> {
                body.append("<li>Desde ").append(slot.getFrom()).append(" hasta ").append(slot.getTo()).append("</li>");
            });
            body.append("</ul>");
        });

        return body.toString();
    }
}
