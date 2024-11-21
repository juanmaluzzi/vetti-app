package org.vetti.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.vetti.model.Appointment;
import org.vetti.model.AppointmentRequest;
import org.vetti.model.Vet;
import org.vetti.service.CalendlyService;
import org.vetti.service.VetService;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {

    private final CalendlyService calendlyService;

    private final VetService vetService;

    public AppointmentController(CalendlyService calendlyService, VetService vetService) {
        this.calendlyService = calendlyService;
        this.vetService = vetService;
    }

    @PostMapping
    public ResponseEntity<Appointment> createAppointment(
            @RequestParam Long vetId,
            @RequestBody AppointmentRequest request) {
        Appointment appointment = calendlyService.scheduleAppointment(
                vetId, request.getEventType(), request.getInviteeName(), request.getInviteeEmail());
        return ResponseEntity.ok(appointment);
    }

    @PutMapping("/{vetId}/calendly-token")
    public ResponseEntity<Vet> updateCalendlyToken(
            @PathVariable Long vetId,
            @RequestParam String calendlyToken) {
        Vet updatedVet = vetService.updateCalendlyToken(vetId, calendlyToken);
        return ResponseEntity.ok(updatedVet);
    }
}
