package org.vetti.service;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.vetti.model.dto.GetUsersEventsDTO;
import org.vetti.model.dto.GetAppointmentsInvitesDTO;
import org.vetti.model.dto.GetAppointmentsDTO;
import org.vetti.response.SearchVetResponse;

import java.util.ArrayList;
import java.util.List;

@Service
public class CalendlyService {

    @Value("${calendly.api.token}")
    private String calendlyApiToken;

    @Value("${calendly.organization.uuid}")
    private String organizationUuid;

    @Autowired
    private VetService vetService;

    private final RestTemplate restTemplate;

    public CalendlyService(RestTemplate restTemplate, VetService vetService) {
        this.restTemplate = restTemplate;
        this.vetService = vetService;
    }

    public List<GetUsersEventsDTO> getMappedEventsByEmail(String email, String status) {
        String url = String.format(
                "https://api.calendly.com/scheduled_events?invitee_email=%s&organization=https://api.calendly.com/organizations/%s&status=%s",
                email, organizationUuid, status
        );

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + calendlyApiToken);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<JsonNode> response = restTemplate.exchange(url, HttpMethod.GET, entity, JsonNode.class);

        JsonNode collectionNode = response.getBody().path("collection");

        List<GetUsersEventsDTO> eventList = new ArrayList<>();
        for (JsonNode eventNode : collectionNode) {
            GetUsersEventsDTO event = new GetUsersEventsDTO();
            event.setCreatedAt(eventNode.path("created_at").asText());
            event.setEndTime(eventNode.path("end_time").asText());
            event.setVetEmail(eventNode.path("event_memberships").get(0).path("user_email").asText());
            event.setVetName(eventNode.path("event_memberships").get(0).path("user_name").asText());
            event.setEventName(eventNode.path("name").asText());
            event.setStartTime(eventNode.path("start_time").asText());
            event.setStatus(eventNode.path("status").asText());
            event.setUpdatedAt(eventNode.path("updated_at").asText());
            event.setLocation(eventNode.path("location").path("location").asText());

            eventList.add(event);
        }

        return eventList;
    }

    public List<GetAppointmentsDTO> getVetsAppointmentsByEmail(String email, String status) {

        String calendlyEmail = getCalendlyEmail(email);

        String urlMemberships = String.format(
                "https://api.calendly.com/organization_memberships?organization=https://api.calendly.com/organizations/%s",
                organizationUuid
        );

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + calendlyApiToken);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<JsonNode> responseCalendly = restTemplate.exchange(urlMemberships, HttpMethod.GET, entity, JsonNode.class);

        JsonNode membershipCollection = responseCalendly.getBody().path("collection");


        String userUri = null;
        // Filtro usuario por mail
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

        //2do llamado para obtener los UUID
        String urlCalendly = String.format(
                "https://api.calendly.com/scheduled_events?user=%s&organization=https://api.calendly.com/organizations/%s&status=%s",
                userUri, organizationUuid, status
        );

        ResponseEntity<JsonNode> responseCalendlySchedules = restTemplate.exchange(urlCalendly, HttpMethod.GET, entity, JsonNode.class);
        JsonNode scheduledCollection = responseCalendlySchedules.getBody().path("collection");


        List<JsonNode> filteredEvents = new ArrayList<>();
        for (JsonNode eventNode : scheduledCollection) {
            JsonNode memberships = eventNode.path("event_memberships");
            for (JsonNode membership : memberships) {
                String userEmail = membership.path("user_email").asText();
                if (userEmail.equalsIgnoreCase(calendlyEmail)) {
                    filteredEvents.add(eventNode);
                    break;
                }
            }
        }

        List<GetAppointmentsDTO> eventInviteesList = new ArrayList<>();

        for (JsonNode eventNode : filteredEvents) {
            String eventUri = eventNode.path("uri").asText();

            String eventUuid = eventUri.substring(eventUri.lastIndexOf("/") + 1);

            String urlInvitees = String.format(
                    "https://api.calendly.com/scheduled_events/%s/invitees?status=%s",
                    eventUuid, status
            );

            // llamada para traer data de invitados (usuarios)
            ResponseEntity<JsonNode> responseInvitees = restTemplate.exchange(urlInvitees, HttpMethod.GET, entity, JsonNode.class);
            JsonNode inviteesCollection = responseInvitees.getBody().path("collection");

            // lista para los invitados (usuarios)
            List<GetAppointmentsInvitesDTO> invitees = new ArrayList<>();
            for (JsonNode inviteeNode : inviteesCollection) {
                GetAppointmentsInvitesDTO invitee = new GetAppointmentsInvitesDTO();
                invitee.setName(inviteeNode.path("name").asText());
                invitee.setEmail(inviteeNode.path("email").asText());
                invitee.setStatus(inviteeNode.path("status").asText());
                invitees.add(invitee);
            }

            GetAppointmentsDTO eventWithInvitees = new GetAppointmentsDTO();
            eventWithInvitees.setCreatedAt(eventNode.path("created_at").asText());
            eventWithInvitees.setEndTime(eventNode.path("end_time").asText());
            eventWithInvitees.setUpdatedAt(eventNode.path("updated_at").asText());
            eventWithInvitees.setEventName(eventNode.path("name").asText());
            eventWithInvitees.setVetEmail(eventNode.path("event_memberships").get(0).path("user_email").asText());
            eventWithInvitees.setVetName(eventNode.path("event_memberships").get(0).path("user_name").asText());
            eventWithInvitees.setStartTime(eventNode.path("start_time").asText());
            eventWithInvitees.setEndTime(eventNode.path("end_time").asText());
            eventWithInvitees.setLocation(eventNode.path("location").path("location").asText());
            eventWithInvitees.setStatus(eventNode.path("status").asText());
            eventWithInvitees.setInvitees(invitees);

            eventInviteesList.add(eventWithInvitees);
        }

        return eventInviteesList;
    }

    private String getCalendlyEmail(String email) {
        ResponseEntity<SearchVetResponse> response = vetService.getVetByEmail(email);
        SearchVetResponse vetResponse = response.getBody();
        String calendlyEmail = vetResponse.getCalendlyEmail();
        return calendlyEmail;
    }
}

