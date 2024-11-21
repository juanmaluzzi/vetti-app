package org.vetti.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.vetti.exceptions.BadRequestException;
import org.vetti.exceptions.NotFoundException;
import org.vetti.model.Vet;
import org.vetti.model.dto.UpdateVetDTO;
import org.vetti.repository.VetRepository;

import static org.vetti.utils.Utils.*;

@Component
public class VetUtils {

    private final VetRepository vetRepository;

    private final Utils utils;

    private final VetUtils vetUtils;

    @Autowired
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public VetUtils(Utils utils, VetRepository vetRepository, PasswordEncoder passwordEncoder, VetUtils vetUtils) {
        this.utils = utils;
        this.vetRepository = vetRepository;
        this.passwordEncoder = passwordEncoder;
        this.vetUtils = vetUtils;
    }

    public void validateVetRegister(Vet vet){
        utils.validateEmail(vet.getEmail(), INVALID_EMAIL);
        utils.validateString(vet.getName(), INVALID_STRING);
        validateAddress(vet.getAddress(), INVALID_STRING);
        validateCuit(vet.getCuit(), INVALID_CUIT);
        utils.validateNotEmpty(vet.getPassword(), INVALID_PASSWORD);
        utils.validatePhoneNumber(vet.getPhoneNumber(), INVALID_PHONENUMBER);
        utils.validateRole(vet.getRole(), INVALID_ROLE);
        if (findVetByEmail(vet.getEmail())) {
            throw new BadRequestException(EMAIL_ALREADY_EXISTS);
        }
        if (findVetByCuit(vet.getCuit())){
            throw new BadRequestException(CUIT_ALREADY_EXISTS);
        }
    }

    public UpdateVetDTO updateVet(Long id, UpdateVetDTO newVetDetails){

        Vet existingVet = vetRepository.findVetById(id)
                .orElseThrow(() -> new NotFoundException("Vet not found with id: " + id));

        if (newVetDetails.getName() != null) {
            utils.validateString(newVetDetails.getName(), INVALID_STRING);
            existingVet.setName(newVetDetails.getName());
        }

        if (newVetDetails.getAddress() != null) {
            validateAddress(newVetDetails.getAddress(), INVALID_STRING);
            existingVet.setAddress(newVetDetails.getAddress());
        }

        if (newVetDetails.getCuit() != null) {
            vetUtils.validateCuit(newVetDetails.getCuit(), INVALID_CUIT);
            existingVet.setCuit(newVetDetails.getCuit());
        }

        if (newVetDetails.getEmail() != null) {
            utils.validateEmail(newVetDetails.getEmail(), INVALID_EMAIL);

            if (findVetByEmail(newVetDetails.getEmail())) {
                throw new BadRequestException(EMAIL_ALREADY_EXISTS);
            }
            existingVet.setEmail(newVetDetails.getEmail());
        }

        if (newVetDetails.getRole() != null) {
            utils.validateRole(newVetDetails.getRole(), INVALID_ROLE);
            existingVet.setRole(newVetDetails.getRole());
        }

        if (newVetDetails.getPhoneNumber() != null) {
            utils.validatePhoneNumber(newVetDetails.getPhoneNumber(), INVALID_PHONENUMBER);
            existingVet.setPhoneNumber(newVetDetails.getPhoneNumber());
        }

        if (newVetDetails.getPassword() != null) {
            utils.validateNotEmpty(newVetDetails.getPassword(), INVALID_PASSWORD);
            existingVet.setPassword(passwordEncoder.encode(newVetDetails.getPassword()));
        }
        Vet updatedVet = vetRepository.save(existingVet);

        return convertToUpdateVetDTO(updatedVet);
    }

    private UpdateVetDTO convertToUpdateVetDTO(Vet vet) {
        UpdateVetDTO dto = new UpdateVetDTO();
        dto.setName(vet.getName());
        dto.setPassword(vet.getPassword());
        dto.setEmail(vet.getEmail());
        dto.setPhoneNumber(vet.getPhoneNumber());
        dto.setRole(vet.getRole());

        return dto;
    }

    private void validateAddress(String value, String errorMessage){
        if (value == null || value.trim().isEmpty() || !value.matches("^[a-zA-ZÀ-ÿ0-9\\s]+$")) throw new BadRequestException(errorMessage + " Received value: " +  value);
    }

    private boolean findVetByEmail(String email) {
        return vetRepository.findVetByEmail(email).isPresent();
    }

    private boolean findVetByCuit(String cuit) {return vetRepository.findVetByCuit(cuit).isPresent();}

    private void validateCuit(String value, String errorMessage){
        if (value == null || value.trim().isEmpty() || !value.matches("^.{1,10}$")) throw new BadRequestException(errorMessage + " Received value: " + value);
    }
}
