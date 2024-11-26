package org.vetti.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.vetti.model.dto.UpdateVetDTO;
import org.vetti.model.request.ScheduleRequest;
import org.vetti.model.request.UserRequest;
import org.vetti.model.request.VetRequest;
import org.vetti.model.response.LoginResponse;
import org.vetti.service.EmailService;
import org.vetti.service.VetService;

import javax.mail.MessagingException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/vet")
@CrossOrigin(origins = "*")
public class VetController {

    @Autowired
    public VetService vetService;

    private final EmailService emailService;

    public VetController(EmailService emailService) {
        this.emailService = emailService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserRequest> registerVet(@RequestBody VetRequest vetRequest) {
        VetRequest registeredvet = vetService.vetRegister(vetRequest);
        return ResponseEntity.status(201).build();
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> loginVet(@RequestBody VetRequest loginVetRequest) {
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
    public ResponseEntity<String> sendScheduleEmail(@RequestBody ScheduleRequest scheduleRequest) throws MessagingException {
        emailService.sendScheduleEmail(scheduleRequest);
        return ResponseEntity.ok("Email enviado correctamente");
    }
}
