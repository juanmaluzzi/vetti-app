package org.vetti.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.vetti.exceptions.NotFoundException;
import org.vetti.model.request.PetRequest;
import org.vetti.repository.PetRepository;
import org.vetti.utils.Utils;

import static org.vetti.utils.Utils.INVALID_STRING;

@Service
public class PetService {

    @Autowired
    private final PetRepository petRepository;

    private final Utils utils;

    @Autowired
    public PetService(PetRepository petRepository, Utils utils){
        this.petRepository = petRepository;
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
}
