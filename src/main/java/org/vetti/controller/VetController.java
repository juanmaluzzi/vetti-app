package org.vetti.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.vetti.model.dto.UpdateVetDTO;
import org.vetti.model.User;
import org.vetti.model.Vet;
import org.vetti.response.LoginResponse;
import org.vetti.service.VetService;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/vet")
@CrossOrigin(origins = "*")
public class VetController {

    @Autowired
    public VetService vetService;

    @PostMapping("/register")
    public ResponseEntity<User> registerVet(@RequestBody Vet vet) {
        Vet registeredvet = vetService.vetRegister(vet);
        return ResponseEntity.status(201).build();
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> loginVet(@RequestBody Vet loginVet) {
        return vetService.loginVet(loginVet.getEmail(), loginVet.getPassword());
    }

    @PatchMapping("/updateVet/{id}")
    public ResponseEntity<?> updateVet(@PathVariable Long id, @RequestBody UpdateVetDTO newUserDetails){

        UpdateVetDTO updateVet = vetService.updateVet(id, newUserDetails);

        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("message", "Vet updated successfully");
        responseBody.put("vetUpdated", updateVet.getEmail());

        return ResponseEntity.status(200).body(responseBody);
    }

    @GetMapping("/searchVetByEmail/{email}")
    public ResponseEntity<?> getVetByEmail(@PathVariable String email){
        return vetService.getVetByEmail(email);
    }

    @GetMapping("/searchVetById/{id}")
    public ResponseEntity<?> getVetById(@PathVariable Long id){
        return vetService.getVetById(id);
    }
}
