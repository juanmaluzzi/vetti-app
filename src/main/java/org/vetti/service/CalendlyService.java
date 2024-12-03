package org.vetti.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.vetti.exceptions.BadRequestException;
import org.vetti.exceptions.NotFoundException;
import org.vetti.model.dto.*;
import org.vetti.model.request.VetRequest;
import org.vetti.model.response.SearchVetResponse;
import org.vetti.repository.VetRepository;

import javax.mail.MessagingException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class CalendlyService {

    @Value("${calendly.api.token}")
    private String calendlyApiToken;

    @Value("${calendly.organization.uuid}")
    private String organizationUuid;

    @Autowired
    private VetService vetService;

    private final RestTemplate restTemplate;

    private final VetRepository vetRepository;

    private final EmailService emailService;

    public CalendlyService(RestTemplate restTemplate, VetService vetService, VetRepository vetRepository, EmailService emailService) {
        this.restTemplate = restTemplate;
        this.vetService = vetService;
        this.vetRepository = vetRepository;
        this.emailService = emailService;
    }

    public List<GetUsersEventsDTO> getMappedEventsByEmail(String email, String status, Boolean expired) {
        // Construir la URL con los parámetros status y email
        String url = String.format(
                "https://api.calendly.com/scheduled_events?invitee_email=%s&organization=https://api.calendly.com/organizations/%s&status=%s",
                email, organizationUuid, status
        );

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + calendlyApiToken);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        // Llamada a Calendly para obtener eventos
        ResponseEntity<JsonNode> response = restTemplate.exchange(url, HttpMethod.GET, entity, JsonNode.class);
        JsonNode collection = response.getBody().path("collection");

        // Crear lista para almacenar eventos filtrados
        List<GetUsersEventsDTO> eventList = new ArrayList<>();
        ZonedDateTime now = ZonedDateTime.now();

        // Filtrar los eventos basados en `expired`
        for (JsonNode events : collection) {
            String endTimeString = events.path("end_time").asText();
            ZonedDateTime endTime = ZonedDateTime.parse(endTimeString, DateTimeFormatter.ISO_ZONED_DATE_TIME)
                    .withZoneSameInstant(ZoneId.of("America/Argentina/Buenos_Aires"));

            // Filtrar por eventos vencidos o futuros según `expired`
            if ((expired != null && expired && endTime.isBefore(now)) || // Vencidos
                    (expired != null && !expired && endTime.isAfter(now)) || // Futuros
                    (expired == null)) { // Sin filtro por fecha

                // Mapear los datos del evento al DTO
                GetUsersEventsDTO event = new GetUsersEventsDTO();
                event.setCreatedAt(events.path("created_at").asText());
                event.setEndTime(events.path("end_time").asText());
                event.setVetEmail(events.path("event_memberships").get(0).path("user_email").asText());
                event.setVetName(events.path("event_memberships").get(0).path("user_name").asText());
                event.setEventName(events.path("name").asText());
                event.setStartTime(events.path("start_time").asText());
                event.setStatus(events.path("status").asText());
                event.setUpdatedAt(events.path("updated_at").asText());
                event.setLocation(events.path("location").path("location").asText());
                event.setEventId(events.path("uri").asText().substring(events.path("uri").asText().lastIndexOf("/") + 1));

                eventList.add(event);
            }
        }

        return eventList;
    }


    public List<GetAppointmentsDTO> getVetsAppointmentsByEmail(String email, String status, Boolean expired) {
        String calendlyEmail = getCalendlyEmail(email);

        if (calendlyEmail == null) {
            throw new IllegalArgumentException("No se encontró un email de Calendlyy para el usuario con email: " + email);
        }

        String urlMemberships = String.format(
                "https://api.calendly.com/organization_memberships?organization=https://api.calendly.com/organizations/%s",
                organizationUuid
        );

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + calendlyApiToken);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        // llamada para obtener todas las veterinarias
        ResponseEntity<JsonNode> responseCalendly = restTemplate.exchange(urlMemberships, HttpMethod.GET, entity, JsonNode.class);
        JsonNode membershipCollection = responseCalendly.getBody().path("collection");

        String userUri = null;

        // filtrar a la veterinaria por email
        for (JsonNode membership : membershipCollection) {
            JsonNode userNode = membership.path("user");
            String userEmail = userNode.path("email").asText();

            if (userEmail.equalsIgnoreCase(calendlyEmail)) {
                userUri = userNode.path("uri").asText();
                break;
            }
        }

        if (userUri == null) {
            throw new RuntimeException("Usuario no encontrado en la organización para el email: " + email);
        }

        String urlCalendly = String.format(
                "https://api.calendly.com/scheduled_events?user=%s&organization=https://api.calendly.com/organizations/%s&status=%s",
                userUri, organizationUuid, status
        );

        ResponseEntity<JsonNode> responseCalendlySchedules = restTemplate.exchange(urlCalendly, HttpMethod.GET, entity, JsonNode.class);
        JsonNode scheduledCollection = responseCalendlySchedules.getBody().path("collection");

        List<JsonNode> filteredEvents = new ArrayList<>();
        ZonedDateTime now = ZonedDateTime.now();

        for (JsonNode events : scheduledCollection) {
            String endTimeString = events.path("end_time").asText();
            ZonedDateTime endTime = ZonedDateTime.parse(endTimeString, DateTimeFormatter.ISO_ZONED_DATE_TIME)
                    .withZoneSameInstant(ZoneId.of("America/Argentina/Buenos_Aires"));

            // validacion para eventos expirados o no
            if ((expired != null && expired && endTime.isBefore(now)) || // vencidos
                    (expired != null && !expired && endTime.isAfter(now))) { // futuros
                JsonNode memberships = events.path("event_memberships");
                for (JsonNode membership : memberships) {
                    String userEmail = membership.path("user_email").asText();
                    if (userEmail.equalsIgnoreCase(calendlyEmail)) {
                        filteredEvents.add(events);
                        break;
                    }
                }
            }
        }

        List<GetAppointmentsDTO> eventInviteesList = new ArrayList<>();

        for (JsonNode events : filteredEvents) {
            String eventUri = events.path("uri").asText();
            String eventUuid = eventUri.substring(eventUri.lastIndexOf("/") + 1);

            String urlInvitees = String.format(
                    "https://api.calendly.com/scheduled_events/%s/invitees",
                    eventUuid
            );

            // llamada para obtener invitados del turno (usuarios)
            ResponseEntity<JsonNode> responseInvitees = restTemplate.exchange(urlInvitees, HttpMethod.GET, entity, JsonNode.class);
            JsonNode inviteesCollection = responseInvitees.getBody().path("collection");

            // usuarios
            List<GetAppointmentsInvitesDTO> invitees = new ArrayList<>();
            for (JsonNode getInvitees : inviteesCollection) {
                GetAppointmentsInvitesDTO invitee = new GetAppointmentsInvitesDTO();
                invitee.setName(getInvitees.path("name").asText());
                invitee.setEmail(getInvitees.path("email").asText());
                invitee.setStatus(getInvitees.path("status").asText());
                invitees.add(invitee);
            }

            // Creo el DTO del evento con los datos filtrados
            GetAppointmentsDTO eventWithInvitees = new GetAppointmentsDTO();
            eventWithInvitees.setCreatedAt(events.path("created_at").asText());
            eventWithInvitees.setEndTime(events.path("end_time").asText());
            eventWithInvitees.setUpdatedAt(events.path("updated_at").asText());
            eventWithInvitees.setEventName(events.path("name").asText());
            eventWithInvitees.setVetEmail(events.path("event_memberships").get(0).path("user_email").asText());
            eventWithInvitees.setVetName(events.path("event_memberships").get(0).path("user_name").asText());
            eventWithInvitees.setStartTime(events.path("start_time").asText());
            eventWithInvitees.setLocation(events.path("location").path("location").asText());
            eventWithInvitees.setStatus(events.path("status").asText());
            eventWithInvitees.setEventId(eventUuid);
            eventWithInvitees.setInvitees(invitees);

            eventInviteesList.add(eventWithInvitees);
        }

        return eventInviteesList;
    }

    private String getCalendlyEmail(String email) {
        ResponseEntity<SearchVetResponse> response = vetService.getVetByEmail(email);

        if (response == null || response.getBody() == null) {
            return null;
        }

        SearchVetResponse vetResponse = response.getBody();

        if ("enabled".equalsIgnoreCase(vetResponse.getStatus())) {
            return vetResponse.getCalendlyEmail();
        }

        return null;
    }

    public List<GetEventsDTO> getEventsList() {
        String url = String.format(
                "https://api.calendly.com/event_types?organization=https://api.calendly.com/organizations/%s",
                organizationUuid
        );

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + calendlyApiToken);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<JsonNode> response = restTemplate.exchange(url, HttpMethod.GET, entity, JsonNode.class);

        JsonNode events = response.getBody().path("collection");

        List<GetEventsDTO> eventList = new ArrayList<>();
        for (JsonNode eventNode : events) {
            GetEventsDTO event = new GetEventsDTO();
            event.setEventName(eventNode.path("name").asText());
            event.setSchedulingUrl(eventNode.path("scheduling_url").asText());
            event.setVetName(eventNode.path("profile").path("name").asText());
            eventList.add(event);
        }

        return eventList;
    }

    public JsonNode cancelCalendlySchedule(String eventId, String reason) {
        String url = String.format(
                "https://api.calendly.com/scheduled_events/%s/cancellation",
                eventId
        );

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + calendlyApiToken);
        headers.set("Content-Type", "application/json");

        CancelCalendlyScheduleDTO requestBody = new CancelCalendlyScheduleDTO();
        requestBody.setReason(reason);

        HttpEntity<CancelCalendlyScheduleDTO> entity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<JsonNode> response = restTemplate.exchange(url, HttpMethod.POST, entity, JsonNode.class);
            return response.getBody();
        } catch (HttpClientErrorException | HttpServerErrorException ex) {
            JsonNode errorBody;
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                errorBody = objectMapper.readTree(ex.getResponseBodyAsString());
            } catch (Exception parseEx) {
                throw new RuntimeException("Error calling Calendly: " + ex.getMessage());
            }
            return errorBody;
        } catch (Exception ex) {
            throw new RuntimeException("Unexpected error occurred: " + ex.getMessage());
        }
    }

    public List<GetEventsDTO> getEventsList(Long id) {

        String vetName = vetRepository.findById(id)
                .map(VetRequest::getName)
                .orElseThrow(() -> new NotFoundException("No se encontró una veterinaria con el ID proporcionado."));

        String url = String.format(
                "https://api.calendly.com/event_types?organization=https://api.calendly.com/organizations/%s",
                organizationUuid
        );

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + calendlyApiToken);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<JsonNode> response = restTemplate.exchange(url, HttpMethod.GET, entity, JsonNode.class);

        JsonNode events = response.getBody().path("collection");

        List<GetEventsDTO> eventList = new ArrayList<>();
        for (JsonNode eventNode : events) {
            String eventVetName = eventNode.path("profile").path("name").asText();
            if (eventVetName.equalsIgnoreCase(vetName)) {
                GetEventsDTO event = new GetEventsDTO();
                event.setEventName(eventNode.path("name").asText());
                event.setSchedulingUrl(eventNode.path("scheduling_url").asText());
                event.setVetName(eventVetName);
                eventList.add(event);
            }
        }

        return eventList;
    }

    public String cancelCalendlyEventTypes(Long id){

        return vetRepository.findById(id)
                .map(VetRequest::getName)
                .orElseThrow(() -> new NotFoundException("No se encontró una veterinaria con el ID proporcionado."));
    }

}

