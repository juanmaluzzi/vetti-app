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

    @Autowired
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public VetUtils(Utils utils, VetRepository vetRepository, PasswordEncoder passwordEncoder) {
        this.utils = utils;
        this.vetRepository = vetRepository;
        this.passwordEncoder = passwordEncoder;
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

        String name = newVetDetails.getName();
        if (name != null && !name.trim().isEmpty()) {
            utils.validateString(name, INVALID_STRING);
            existingVet.setName(name);
        }

        String address = newVetDetails.getAddress();
        if (address != null && !address.trim().isEmpty()) {
            validateAddress(address, INVALID_STRING);
            existingVet.setAddress(address);
        }

        String cuit = newVetDetails.getCuit();
        if (cuit != null && !cuit.trim().isEmpty()) {
            validateCuit(cuit, INVALID_CUIT);
            existingVet.setCuit(cuit);
        }

        String email = newVetDetails.getEmail();
        if (email != null && !email.trim().isEmpty()) {
            utils.validateEmail(email, INVALID_EMAIL);

            if (findVetByEmail(email)) {
                throw new BadRequestException(EMAIL_ALREADY_EXISTS);
            }
            existingVet.setEmail(email);
        }

        String role = newVetDetails.getRole();
        if (role != null && !role.trim().isEmpty() ) {
            utils.validateRole(role, INVALID_ROLE);
            existingVet.setRole(role);
        }

        String phoneNumber = newVetDetails.getPhoneNumber();
        if (phoneNumber != null && !phoneNumber.trim().isEmpty()) {
            utils.validatePhoneNumber(phoneNumber, INVALID_PHONENUMBER);
            existingVet.setPhoneNumber(phoneNumber);
        }

        String password = newVetDetails.getPassword();
        if (password != null && !password.trim().isEmpty()) {
            utils.validateNotEmpty(password, INVALID_PASSWORD);
            existingVet.setPassword(passwordEncoder.encode(password));
        }

        String status = newVetDetails.getStatus();
        if(status != null && !status.trim().isEmpty()){
            utils.validateString(status, INVALID_STATUS);
            existingVet.setStatus(status);
        }

        if (newVetDetails.getIsEmergencyVet() != null) {
            utils.validateBoolean(newVetDetails.getIsEmergencyVet(), INVALID_EMERGENCY_VET);
            existingVet.setIsEmergencyVet(newVetDetails.getIsEmergencyVet());
        }

        String calendlyEmail = newVetDetails.getCalendlyEmail();
        if(calendlyEmail != null){
            utils.validateEmail(calendlyEmail, INVALID_EMERGENCY_VET);
            existingVet.setCalendlyEmail(calendlyEmail);
        }

        String calendlyCalendar = newVetDetails.getCalendlyCalendar();
        if(calendlyCalendar != null){
            existingVet.setCalendlyCalendar(calendlyCalendar);
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
        dto.setIsEmergencyVet(vet.getIsEmergencyVet());

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
        if (value == null || value.trim().isEmpty() || !value.matches("^\\d{8,10}$")) throw new BadRequestException(errorMessage + " Received value: " + value);
    }
}
