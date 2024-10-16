package org.vetti.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.vetti.exceptions.NotFoundException;
import org.vetti.model.Pet;
import org.vetti.model.User;
import org.vetti.repository.PetRepository;
import org.vetti.repository.UserRepository;
import org.vetti.service.PetService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/pets")
@CrossOrigin(origins = "*")
public class PetController {

    @Autowired
    private PetRepository petRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PetService petService;

    @GetMapping
    public List<Pet> getAllPets() {
        return petRepository.findAll();
    }

    @PostMapping("/addPet/{userId}")
    public ResponseEntity<Pet> createPet(@PathVariable Long userId, @RequestBody Pet pet) {

        User user = userRepository.findUserById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with ID: " + userId));

        pet.setUser(user);

        Pet newPet = petRepository.save(pet);

        return ResponseEntity.ok(newPet);
    }

    @PatchMapping("/updatePet/{id}")
    public ResponseEntity<?> updatePet(@PathVariable Long id, @RequestBody Pet newPetDetails){

        Pet updatePet = petService.updatePet(id, newPetDetails);

        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("message", "Pet updated successfully");
        responseBody.put("petId: ", updatePet.getId());

        return ResponseEntity.status(200).body(responseBody);
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<Pet> getPetById(@PathVariable Long id) {
        Pet pet = petService.getPetsById(id);
        return ResponseEntity.ok(pet);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deletePet(@PathVariable Long id) {
        petService.deletePetById(id);
        return ResponseEntity.ok("Pet successfully deleted.");
    }
}
