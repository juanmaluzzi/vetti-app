package org.vetti.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.vetti.exceptions.NotFoundException;
import org.vetti.model.request.PetRequest;
import org.vetti.model.request.UserRequest;
import org.vetti.repository.PetRepository;
import org.vetti.repository.UserRepository;
import org.vetti.utils.Utils;

import java.util.List;

import static org.vetti.utils.Utils.INVALID_STRING;

@Service
public class PetService {

    @Autowired
    private final PetRepository petRepository;

    @Autowired
    private final UserRepository userRepository;

    private final Utils utils;

    @Autowired
    public PetService(PetRepository petRepository, UserRepository userRepository, Utils utils){
        this.petRepository = petRepository;
        this.userRepository = userRepository;
        this.utils = utils;
    }

    public PetRequest updatePet(Long id, PetRequest newPetRequestDetails){

        PetRequest existingPetRequest = petRepository.findPetById(id)
                .orElseThrow(() -> new NotFoundException("Pet not found with id: " + id));

        if (newPetRequestDetails.getName() != null) {
            utils.validateString(newPetRequestDetails.getName(), INVALID_STRING);
            existingPetRequest.setName(newPetRequestDetails.getName());
        }

        if (newPetRequestDetails.getType() != null) {
            utils.validateString(newPetRequestDetails.getType(), INVALID_STRING);
            existingPetRequest.setType(newPetRequestDetails.getType());
        }

        if (newPetRequestDetails.getBirthday() != null) {
            existingPetRequest.setType(newPetRequestDetails.getBirthday());
        }

        return petRepository.save(existingPetRequest);
    }

    public PetRequest getPetsById(Long id){
        return petRepository.findPetById(id)
                .orElseThrow(() -> new NotFoundException("Pet not found with ID: " + id));
    }

    public void deletePetById(Long id) {
        PetRequest petRequest = petRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Pet not found with ID: " + id));

        petRepository.delete(petRequest);
    }

    public PetRequest createPet(Long userId, PetRequest petRequest) {
        UserRequest userRequest = userRepository.findUserById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with ID: " + userId));

        petRequest.setUserRequest(userRequest);
        return petRepository.save(petRequest);
    }

    public List<PetRequest> getAllPets() {
        return petRepository.findAll();
    }


}
