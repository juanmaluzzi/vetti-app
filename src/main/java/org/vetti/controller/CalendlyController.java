package org.vetti.controller;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.vetti.model.dto.GetUsersEventsDTO;
import org.vetti.model.dto.GetAppointmentsDTO;
import org.vetti.service.CalendlyService;
import org.vetti.utils.Utils;

import java.util.List;


@RequestMapping("/calendly")
@RestController
@AllArgsConstructor
public class CalendlyController {

    @Autowired
    private CalendlyService calendlyService;

    private Utils utils;

    @GetMapping("/user/appointments/{email}")
    public List<GetUsersEventsDTO> getUserAppointments(@PathVariable String email, @RequestParam (defaultValue = "active") String status) {
        utils.validateStatus(status);
        return calendlyService.getMappedEventsByEmail(email, status);
    }

    @GetMapping("/vet/appointments/{email}")
    public List<GetAppointmentsDTO> getVetAppointments(@PathVariable String email, @RequestParam (defaultValue = "active") String status) {
        utils.validateStatus(status);
        return calendlyService.getVetsAppointmentsByEmail(email, status);
    }
}
