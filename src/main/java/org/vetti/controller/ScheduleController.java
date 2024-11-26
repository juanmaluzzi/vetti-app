package org.vetti.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.vetti.model.request.ScheduleRequest;
import org.vetti.service.EmailService;

import javax.mail.MessagingException;

@RestController
@RequestMapping("/api/schedules")
public class ScheduleController {

    private final EmailService emailService;

    public ScheduleController(EmailService emailService) {
        this.emailService = emailService;
    }

    @PostMapping("/email")
    public ResponseEntity<String> sendScheduleEmail(@RequestBody ScheduleRequest scheduleRequest) throws MessagingException {
        emailService.sendScheduleEmail(scheduleRequest);
        return ResponseEntity.ok("Email enviado correctamente");
    }
}