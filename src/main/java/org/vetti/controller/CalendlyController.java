package org.vetti.controller;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.vetti.model.dto.CancelCalendlyScheduleDTO;
import org.vetti.model.dto.GetEventsDTO;
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
    public List<GetUsersEventsDTO> getUserAppointments(@PathVariable String email, @RequestParam (defaultValue = "active") String status, @RequestParam (defaultValue = "false") Boolean expired) {
        utils.validateStatus(status);
        return calendlyService.getMappedEventsByEmail(email, status, expired);
    }

    @GetMapping("/vet/appointments/{email}")
    public List<GetAppointmentsDTO> getVetAppointments(@PathVariable String email, @RequestParam (defaultValue = "active") String status, @RequestParam(defaultValue = "false") Boolean expired) {
        utils.validateStatus(status);
        return calendlyService.getVetsAppointmentsByEmail(email, status, expired);
    }

    @GetMapping("/vet/events")
    public List<GetEventsDTO> getEvents(){
        return calendlyService.getEventsList();
    }

    @PostMapping("/scheduled_events/cancellation")
    public ResponseEntity<?> cancelEvent(@RequestBody CancelCalendlyScheduleDTO cancelRequest) {
        try {
            JsonNode response = calendlyService.cancelCalendlySchedule(
                    cancelRequest.getEventId(),
                    cancelRequest.getReason()
            );

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

}
