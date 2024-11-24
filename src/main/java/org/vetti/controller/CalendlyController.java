package org.vetti.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.vetti.model.dto.GetUsersEventsDTO;
import org.vetti.model.dto.GetAppointmentsDTO;
import org.vetti.service.CalendlyService;

import java.util.List;


@RequestMapping("/calendly")
@RestController
public class CalendlyController {

    @Autowired
    private CalendlyService calendlyService;

    @GetMapping("/user/appointments/{email}")
    public List<GetUsersEventsDTO> getUserAppointments(@PathVariable String email, @RequestParam (defaultValue = "active") String status) {
        return calendlyService.getMappedEventsByEmail(email, status);
    }

    @GetMapping("/vet/appointments/{email}")
    public List<GetAppointmentsDTO> getVetAppointments(@PathVariable String email, @RequestParam (defaultValue = "active") String status) {
        return calendlyService.getVetsAppointmentsByEmail(email, status);
    }
}
