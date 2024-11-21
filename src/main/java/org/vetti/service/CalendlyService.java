package org.vetti.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.vetti.model.Appointment;
import org.vetti.model.Vet;
import org.vetti.repository.AppointmentRepository;
import org.vetti.repository.VetRepository;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class CalendlyService {

    private final VetRepository vetRepository;
    private final AppointmentRepository appointmentRepository;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${calendly.api.base-url}")
    private String baseUrl;

    public CalendlyService(VetRepository vetRepository, AppointmentRepository appointmentRepository, RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.vetRepository = vetRepository;
        this.appointmentRepository = appointmentRepository;
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    public Appointment scheduleAppointment(Long vetId, String eventType, String inviteeName, String inviteeEmail) {
        // Obtener el token de Calendly de la veterinaria
        Vet vet = vetRepository.findById(vetId)
                .orElseThrow(() -> new RuntimeException("Veterinaria no encontrada"));

        String apiToken = vet.getCalendlyToken();
        if (apiToken == null) {
            throw new RuntimeException("El token de Calendly no está configurado para esta veterinaria");
        }

        // Configurar la URL y los headers
        String url = baseUrl + "/scheduled_events";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + apiToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Crear el cuerpo de la solicitud
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("event_type", eventType);
        requestBody.put("invitee_name", inviteeName);
        requestBody.put("invitee_email", inviteeEmail);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        // Hacer la solicitud a la API de Calendly
        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

        // Parsear la respuesta para obtener los detalles de la cita
        Appointment appointment = new Appointment();
        try {
            JsonNode root = objectMapper.readTree(response.getBody());
            JsonNode eventNode = root.path("resource");

            appointment.setVet(vet);
            appointment.setEventType(eventType);
            appointment.setInviteeName(inviteeName);
            appointment.setInviteeEmail(inviteeEmail);

            // Extraer start_time y end_time de la respuesta de Calendly
            String startTimeStr = eventNode.path("start_time").asText();
            String endTimeStr = eventNode.path("end_time").asText();

            appointment.setStartTime(LocalDateTime.parse(startTimeStr));
            appointment.setEndTime(LocalDateTime.parse(endTimeStr));
        } catch (Exception e) {
            throw new RuntimeException("Error al parsear la respuesta de Calendly", e);
        }

        // Guardar la cita en la base de datos
        return appointmentRepository.save(appointment);
    }
}