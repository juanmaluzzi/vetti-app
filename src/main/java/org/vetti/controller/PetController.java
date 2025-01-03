package org.vetti.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.vetti.model.request.PetRequest;
import org.vetti.service.PetService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/pets")
@CrossOrigin(origins = "*")
public class PetController {

    @Autowired
    private PetService petService;

    @GetMapping("/getAll")
    public List<PetRequest> getAllPets() {
        return petService.getAllPets();
    }

    @PostMapping("/addPet/{userId}")
    public ResponseEntity<PetRequest> createPet(@PathVariable Long userId, @RequestBody PetRequest petRequest) {
        PetRequest newPetRequest = petService.createPet(userId, petRequest);
        return ResponseEntity.ok(newPetRequest);
    }

    @PatchMapping("/updatePet/{id}")
    public ResponseEntity<?> updatePet(@PathVariable Long id, @RequestBody PetRequest newPetRequestDetails){

        PetRequest updatePetRequest = petService.updatePet(id, newPetRequestDetails);

        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("message", "Pet updated successfully");
        responseBody.put("petId: ", updatePetRequest.getId());

        return ResponseEntity.status(200).body(responseBody);
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<PetRequest> getPetById(@PathVariable Long id) {
        PetRequest petRequest = petService.getPetsById(id);
        return ResponseEntity.ok(petRequest);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deletePet(@PathVariable Long id) {
        petService.deletePetById(id);
        return ResponseEntity.ok("Pet successfully deleted.");
    }
}
