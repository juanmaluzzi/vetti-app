package org.vetti.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.vetti.model.dto.CanceledEventCalendlyDTO;
import org.vetti.model.dto.CheckPaymentStatusDTO;
import org.vetti.model.dto.ServiceAddedCalendlyDTO;
import org.vetti.model.dto.UpdateVetDTO;
import org.vetti.model.request.ScheduleRequest;
import org.vetti.model.request.UserRequest;
import org.vetti.model.request.VetRequest;
import org.vetti.model.response.LoginVetResponse;
import org.vetti.model.response.SearchVetResponse;
import org.vetti.repository.VetRepository;
import org.vetti.service.EmailService;
import org.vetti.service.VetService;

import javax.mail.MessagingException;
import javax.persistence.EntityNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/vet")
@CrossOrigin(origins = "*")
public class VetController {

    @Autowired
    public VetService vetService;

    public VetRepository vetRepository;

    private final EmailService emailService;

    public VetController(EmailService emailService, VetRepository vetRepository) {
        this.emailService = emailService;
        this.vetRepository = vetRepository;
    }

    @PostMapping("/register")
    public ResponseEntity<UserRequest> registerVet(@RequestBody VetRequest vetRequest) throws MessagingException, IOException {
        vetService.vetRegister(vetRequest);
        emailService.sendRegisteredVet(vetRequest);
        return ResponseEntity.status(201).build();
    }

    @PostMapping("/login")
    public ResponseEntity<LoginVetResponse> loginVet(@RequestBody VetRequest loginVetRequest) {
        return vetService.loginVet(loginVetRequest.getEmail(), loginVetRequest.getPassword());
    }

    @PatchMapping("/updateVet/{id}")
    public ResponseEntity<?> updateVet(@PathVariable Long id, @RequestBody UpdateVetDTO newUserDetails){

        UpdateVetDTO updateVet = vetService.updateVet(id, newUserDetails);

        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("message", "Vet updated successfully");
        responseBody.put("vetUpdated", updateVet.getEmail());

        return ResponseEntity.status(200).body(responseBody);
    }

    @PatchMapping("/updateVetStatusByEmail")
    public ResponseEntity<?> updateVetStatusByEmail(
            @RequestParam String email,
            @RequestBody Map<String, String> requestBody) {

        String newStatus = requestBody.get("status");

        if (newStatus == null || newStatus.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Status is required"));
        }

        try {
            vetService.updateVetStatusByEmail(email, newStatus);

            if ("enabled".equalsIgnoreCase(newStatus)) {
                VetRequest vet = vetRepository.findVetByEmail(email)
                        .orElseThrow(() -> new EntityNotFoundException("Vet not found with email: " + email));
                emailService.sendVetEnabledNotification(vet.getEmail(), vet.getName());
                emailService.sendConfirmationToAdmin(vet.getName());
            }

            return ResponseEntity.ok(Map.of("message", "Vet status updated successfully"));

        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Error updating vet status", "error", e.getMessage()));
        }
    }



    @GetMapping("/searchVets")
    public ResponseEntity<?> getAllVets(){
        return vetService.getAllVets();
    }

    @GetMapping("/searchVetByEmail/{email}")
    public ResponseEntity<?> getVetByEmail(@PathVariable String email){
        return vetService.getVetByEmail(email);
    }

    @GetMapping("/searchVetById/{id}")
    public ResponseEntity<?> getVetById(@PathVariable Long id){
        return vetService.getVetById(id);
    }

    @PostMapping("/schedules")
    public ResponseEntity<String> sendScheduleEmail(@RequestBody ScheduleRequest scheduleRequest) throws MessagingException, IOException {
        emailService.sendScheduleEmail(scheduleRequest);
        return ResponseEntity.ok("Email enviado correctamente");
    }

    @PostMapping("/sendServiceAdded")
    public ResponseEntity<String> sendServiceAddedEmail(@RequestBody ServiceAddedCalendlyDTO request) {
        try {
            emailService.sendServiceAddedEmailToVet(request);
            return ResponseEntity.ok("Correo de confirmación de servicios agregados enviado correctamente.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al enviar el correo de confirmación: " + e.getMessage());
        }
    }

    @PostMapping("/sendServiceDeleted")
    public ResponseEntity<String> sendServiceDeletionEmail(@RequestBody CanceledEventCalendlyDTO request) {
        try {
            emailService.sendServiceDeletionEmailToVet(request);
            return ResponseEntity.ok("Correo de cancelación de servicio enviado correctamente.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al enviar el correo de cancelación: " + e.getMessage());
        }
    }

}
