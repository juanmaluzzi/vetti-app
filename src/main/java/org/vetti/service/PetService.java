package org.vetti.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.vetti.exceptions.NotFoundException;
import org.vetti.model.Pet;
import org.vetti.model.User;
import org.vetti.model.dto.UpdateUserDTO;
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

    public Pet updatePet(Long id, Pet newPetDetails){

        Pet existingPet = petRepository.findPetById(id)
                .orElseThrow(() -> new NotFoundException("Pet not found with id: " + id));

        if (newPetDetails.getName() != null) {
            utils.validateString(newPetDetails.getName(), INVALID_STRING);
            existingPet.setName(newPetDetails.getName());
        }

        if (newPetDetails.getType() != null) {
            utils.validateString(newPetDetails.getType(), INVALID_STRING);
            existingPet.setType(newPetDetails.getType());
        }

        return petRepository.save(existingPet);
    }

    public Pet getPetsById(Long id){
        return petRepository.findPetById(id)
                .orElseThrow(() -> new NotFoundException("Pet not found with ID: " + id));
    }

    public void deletePetById(Long id) {
        Pet pet = petRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Pet not found with ID: " + id));

        petRepository.delete(pet);
    }
}
