package org.vetti.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.vetti.model.request.ScheduleRequest;
import org.vetti.model.request.VetRequest;

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
        helper.setText(buildScheduleEmailBody(scheduleRequest), true);

        mailSender.send(message);
        System.out.println("Correo enviado a " + emailTo);
    }

    private String buildScheduleEmailBody(ScheduleRequest scheduleRequest) {
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

    public void sendRegisteredVet(VetRequest vetRequest) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setTo(emailTo);
        helper.setSubject("REGISTRO - Nueva veterinaria registrada: " + vetRequest.getName());
        helper.setText(buildRegisteredVetEmailBody(vetRequest), true);

        mailSender.send(message);
        System.out.println("Correo enviado a " + emailTo);
    }

    private String buildRegisteredVetEmailBody(VetRequest vetRequest) {
        StringBuilder body = new StringBuilder();

        body.append("<h2>Detalles de la Veterinaria:</h2>");
        body.append("<p><strong>Nombre:</strong> ").append(vetRequest.getName()).append("</p>");
        body.append("<p><strong>Email:</strong> ").append(vetRequest.getEmail()).append("</p>");
        body.append("<p><strong>Dirección:</strong> ").append(vetRequest.getAddress()).append("</p>");
        body.append("<p><strong>Localidad:</strong> ").append(vetRequest.getDistrict()).append("</p>");
        body.append("<p><strong>Telefono:</strong> ").append(vetRequest.getPhoneNumber()).append("</p>");

        return body.toString();
    }

    public void sendPasswordResetEmail(String email, String resetCode) {
        String subject = "Recuperación de Contraseña";
        String body = String.format(
                "Hola,\n\nHemos recibido una solicitud para restablecer tu contraseña. " +
                        "Usa el siguiente código para continuar:\n\n%s\n\nEste código es válido por 15 minutos.\n\n" +
                        "Si no solicitaste este cambio, puedes ignorar este mensaje.",
                resetCode
        );

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject(subject);
        message.setText(body);

        mailSender.send(message);

        System.out.println("Correo enviado a: " + email);
    }

    public void sendCancelEventsVet(String name, String eventName) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setTo(emailTo);
        helper.setSubject("BACKOFFICE - Cancelación de eventos");
        helper.setText(cancelCalendlyEventTypesBody(name, eventName), true);

        mailSender.send(message);
        System.out.println("Correo enviado a " + emailTo);
    }

    public String cancelCalendlyEventTypesBody(String name, String eventName) {
        StringBuilder body = new StringBuilder();

        body.append("<h2>BACKOFFICE - Cancelación de eventos:</h2>");
        body.append("<p><strong>Veterinaria:</strong> ").append(name).append("</p>");
        body.append("<p><strong>Evento a cancelar:</strong> ").append(eventName).append("</p>");


        return body.toString();
    }
}
