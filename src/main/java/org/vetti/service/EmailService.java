package org.vetti.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.vetti.model.dto.CanceledEventCalendlyDTO;
import org.vetti.model.dto.ServiceAddedCalendlyDTO;
import org.vetti.model.request.ScheduleRequest;
import org.vetti.model.request.VetRequest;
import org.vetti.templates.LoadTemplate;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${email.to}")
    private String emailTo;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendScheduleEmail(ScheduleRequest scheduleRequest) throws MessagingException, IOException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setTo(emailTo);
        helper.setSubject("BACKOFFICE - Nuevo servicio recibido para " + scheduleRequest.getVetName());

        LoadTemplate templateLoader = new LoadTemplate();
        StringBuilder scheduleHtml = new StringBuilder();

        Set<String> processedDays = new HashSet<>();
        scheduleRequest.getDays().forEach(day -> {
            if (!processedDays.contains(day.getDay())) {
                processedDays.add(day.getDay());
                scheduleHtml.append("<hr>");
                scheduleHtml.append("<p><strong>Día:</strong> ").append(day.getDay()).append("</p>");
                scheduleHtml.append("<ul>");
                day.getTimeSlots().forEach(slot -> {
                    scheduleHtml.append("<li>Desde ").append(slot.getFrom())
                            .append(" hasta ").append(slot.getTo()).append("</li>");
                });
                scheduleHtml.append("</ul>");
            }
        });

        Map<String, String> variables = Map.of(
                "vetName", scheduleRequest.getVetName(),
                "vetEmail", scheduleRequest.getVetEmail(),
                "vetService", scheduleRequest.getService(),
                "vetSchedule", scheduleHtml.toString()
        );
        String emailBody = templateLoader.loadTemplate("schedule_received_notification.html", variables);
        helper.setText(emailBody, true);

        ClassPathResource logo = new ClassPathResource("static/vettilogo.png");
        helper.addInline("logoVetti", logo);

        mailSender.send(message);
        System.out.println("Correo enviado a " + emailTo);
    }


    public void sendRegisteredVet(VetRequest vetRequest) throws MessagingException, IOException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setTo(emailTo);
        helper.setSubject("REGISTRO - Nueva veterinaria registrada: " + vetRequest.getName());

        LoadTemplate templateLoader = new LoadTemplate();
        Map<String, String> variables = Map.of(
                "vetName", vetRequest.getName(),
                "vetEmail", vetRequest.getEmail(),
                "vetAddress", vetRequest.getAddress(),
                "vetDistrict", vetRequest.getDistrict(),
                "vetPhone", vetRequest.getPhoneNumber()
        );
        String emailBody = templateLoader.loadTemplate("registered_vet_notification.html", variables);
        helper.setText(emailBody, true);

        ClassPathResource logo = new ClassPathResource("static/vettilogo.png");
        helper.addInline("logoVetti", logo);

        mailSender.send(message);
        System.out.println("Correo enviado a: " + emailTo);
    }

    public void sendPasswordResetEmail(String email, String resetCode) throws MessagingException, IOException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        LoadTemplate templateLoader = new LoadTemplate();
        Map<String, String> variables = Map.of(
                "resetCode", resetCode
        );
        String emailBody = templateLoader.loadTemplate("password_reset.html", variables);

        helper.setTo(email);
        helper.setSubject("Recuperación de Contraseña");
        helper.setText(emailBody, true);

        ClassPathResource logo = new ClassPathResource("static/vettilogo.png");
        helper.addInline("logoVetti", logo);

        mailSender.send(message);
        System.out.println("Correo enviado a: " + email);
    }

    public void sendCancelEventsVet(String vetName, String eventName) throws MessagingException, IOException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        LoadTemplate templateLoader = new LoadTemplate();
        Map<String, String> variables = Map.of(
                "vetName", vetName,
                "eventName", eventName
        );
        String emailBody = templateLoader.loadTemplate("cancel_calendly_event.html", variables);

        helper.setTo(emailTo);
        helper.setSubject("BACKOFFICE - Cancelación de eventos");
        helper.setText(emailBody, true);

        ClassPathResource logo = new ClassPathResource("static/vettilogo.png");
        helper.addInline("logoVetti", logo);

        mailSender.send(message);
        System.out.println("Correo enviado a " + emailTo);
    }


    public void sendPaymentConfirmationToVet(String vetName, String vetEmail) throws MessagingException, IOException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        LoadTemplate templateLoader = new LoadTemplate();
        Map<String, String> variables = Map.of("vetName", vetName);
        String emailBody = templateLoader.loadTemplate("payment_confirmation.html", variables);

        helper.setTo(vetEmail);
        helper.setSubject("Confirmación de Pago - Bienvenido a Vetti");
        helper.setText(emailBody, true);

        ClassPathResource logo = new ClassPathResource("static/vettilogo.png");
        helper.addInline("logoVetti", logo);

        mailSender.send(message);
        System.out.println("Correo de confirmación enviado a " + vetEmail);
    }

    public void sendPaymentConfirmationToAdmin(VetRequest vetRequest) throws MessagingException, IOException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        LoadTemplate templateLoader = new LoadTemplate();
        Map<String, String> variables = Map.of(
                "vetName", vetRequest.getName(),
                "vetEmail", vetRequest.getEmail(),
                "vetAddress", vetRequest.getAddress(),
                "vetDistrict", vetRequest.getDistrict(),
                "vetPhone", vetRequest.getPhoneNumber()
        );
        String emailBody = templateLoader.loadTemplate("payment_confirmation_admin.html", variables);

        helper.setTo(emailTo);
        helper.setSubject("Nueva veterinaria registrada - Suscripción confirmada");
        helper.setText(emailBody, true);

        ClassPathResource logo = new ClassPathResource("static/vettilogo.png");
        helper.addInline("logoVetti", logo);

        mailSender.send(message);
        System.out.println("Correo de confirmación enviado al administrador.");
    }

    public void sendVetEnabledNotification(String vetEmail, String vetName) throws MessagingException, IOException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        LoadTemplate templateLoader = new LoadTemplate();
        Map<String, String> variables = Map.of(
                "vetName", vetName
        );
        String emailBody = templateLoader.loadTemplate("vet_enabled_notification.html", variables);

        helper.setTo(vetEmail);
        helper.setSubject("Cuenta Habilitada - Bienvenido a Vetti");
        helper.setText(emailBody, true);

        ClassPathResource logo = new ClassPathResource("static/vettilogo.png");
        helper.addInline("logoVetti", logo);

        mailSender.send(message);
        System.out.println("Correo de habilitación enviado a " + vetEmail);
    }


    public void sendServiceAddedEmailToVet(ServiceAddedCalendlyDTO request) throws MessagingException, IOException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        LoadTemplate templateLoader = new LoadTemplate();

        String[] servicesArray = request.getDescription().split(",");
        String servicesHtml = Arrays.stream(servicesArray)
                .map(String::trim)
                .map(service -> "<li>" + service + "</li>")
                .collect(Collectors.joining());

        Map<String, String> variables = Map.of(
                "vetName", request.getVetName(),
                "description", servicesHtml
        );

        String emailBody = templateLoader.loadTemplate("service_added.html", variables);

        helper.setTo(request.getVetEmail());
        helper.setSubject("Confirmación de Servicios Agregados");
        helper.setText(emailBody, true);

        ClassPathResource logo = new ClassPathResource("static/vettilogo.png");
        helper.addInline("logoVetti", logo);

        mailSender.send(message);
        System.out.println("Correo de confirmación enviado a " + request.getVetEmail());
    }


    public void sendServiceDeletionEmailToVet(CanceledEventCalendlyDTO request) throws MessagingException, IOException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        LoadTemplate templateLoader = new LoadTemplate();
        Map<String, String> variables = Map.of(
                "vetName", request.getVetName(),
                "eventName", request.getEventName()
        );
        String emailBody = templateLoader.loadTemplate("service_deleted.html", variables);

        helper.setTo(request.getVetEmail());
        helper.setSubject("Confirmación de Cancelación de Servicios");
        helper.setText(emailBody, true);

        ClassPathResource logo = new ClassPathResource("static/vettilogo.png");
        helper.addInline("logoVetti", logo);

        mailSender.send(message);
        System.out.println("Correo de cancelación enviado.");
    }

    public void sendConfirmationToAdmin(String vetName) throws MessagingException, IOException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        LoadTemplate templateLoader = new LoadTemplate();
        Map<String, String> variables = Map.of(
                "vetName", vetName
        );
        String emailBody = templateLoader.loadTemplate("confirmation_to_admin.html", variables);

        helper.setTo(emailTo);
        helper.setSubject("Confirmación: Veterinaria Habilitada");
        helper.setText(emailBody, true);

        ClassPathResource logo = new ClassPathResource("static/vettilogo.png");
        helper.addInline("logoVetti", logo);

        mailSender.send(message);
        System.out.println("Correo de confirmación enviado a vetfinal@gmail.com");
    }


}
