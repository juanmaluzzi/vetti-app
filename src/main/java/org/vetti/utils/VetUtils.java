package org.vetti.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.vetti.exceptions.BadRequestException;
import org.vetti.exceptions.NotFoundException;
import org.vetti.model.request.VetRequest;
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

    public void validateVetRegister(VetRequest vetRequest){
        utils.validateEmail(vetRequest.getEmail(), INVALID_EMAIL);
        utils.validateString(vetRequest.getName(), INVALID_STRING);
        validateAddress(vetRequest.getAddress(), INVALID_STRING);
        validateCuit(vetRequest.getCuit(), INVALID_CUIT);
        utils.validateNotEmpty(vetRequest.getPassword(), INVALID_PASSWORD);
        utils.validatePhoneNumber(vetRequest.getPhoneNumber(), INVALID_PHONENUMBER);
        utils.validateRole(vetRequest.getRole(), INVALID_ROLE);
        if (findVetByEmail(vetRequest.getEmail())) {
            throw new BadRequestException(EMAIL_ALREADY_EXISTS);
        }
        if (findVetByCuit(vetRequest.getCuit())){
            throw new BadRequestException(CUIT_ALREADY_EXISTS);
        }
    }

    public UpdateVetDTO updateVet(Long id, UpdateVetDTO newVetDetails){

        VetRequest existingVetRequest = vetRepository.findVetById(id)
                .orElseThrow(() -> new NotFoundException("Vet not found with id: " + id));

        String name = newVetDetails.getName();
        if (name != null && !name.trim().isEmpty()) {
            utils.validateString(name, INVALID_STRING);
            existingVetRequest.setName(name);
        }

        String address = newVetDetails.getAddress();
        if (address != null && !address.trim().isEmpty()) {
            validateAddress(address, INVALID_ADDRESS);
            existingVetRequest.setAddress(address);
        }

        String district = newVetDetails.getDistrict();
        if (district != null && !district.trim().isEmpty()) {
            validateAddress(district, INVALID_DISTRICT);
            existingVetRequest.setDistrict(district);
        }

        String cuit = newVetDetails.getCuit();
        if (cuit != null && !cuit.trim().isEmpty()) {
            validateCuit(cuit, INVALID_CUIT);
            existingVetRequest.setCuit(cuit);
        }

        String email = newVetDetails.getEmail();
        if (email != null && !email.trim().isEmpty()) {
            utils.validateEmail(email, INVALID_EMAIL);

            if (findVetByEmail(email)) {
                throw new BadRequestException(EMAIL_ALREADY_EXISTS);
            }
            existingVetRequest.setEmail(email);
        }

        String role = newVetDetails.getRole();
        if (role != null && !role.trim().isEmpty() ) {
            utils.validateRole(role, INVALID_ROLE);
            existingVetRequest.setRole(role);
        }

        String phoneNumber = newVetDetails.getPhoneNumber();
        if (phoneNumber != null && !phoneNumber.trim().isEmpty()) {
            utils.validatePhoneNumber(phoneNumber, INVALID_PHONENUMBER);
            existingVetRequest.setPhoneNumber(phoneNumber);
        }

        String password = newVetDetails.getPassword();
        if (password != null && !password.trim().isEmpty()) {
            utils.validateNotEmpty(password, INVALID_PASSWORD);
            existingVetRequest.setPassword(passwordEncoder.encode(password));
        }

        String status = newVetDetails.getStatus();
        if(status != null && !status.trim().isEmpty()){
            utils.validateString(status, INVALID_STATUS);
            validateStatus(status, INVALID_STATUSFIELD);
            existingVetRequest.setStatus(status);
        }

        if (newVetDetails.getIsEmergencyVet() != null) {
            utils.validateBoolean(newVetDetails.getIsEmergencyVet(), INVALID_EMERGENCY_VET);
            existingVetRequest.setIsEmergencyVet(newVetDetails.getIsEmergencyVet());
        }

        String calendlyEmail = newVetDetails.getCalendlyEmail();
        if(calendlyEmail != null){
            utils.validateEmail(calendlyEmail, INVALID_EMERGENCY_VET);
            existingVetRequest.setCalendlyEmail(calendlyEmail);
        }

        String calendlyCalendar = newVetDetails.getCalendlyCalendar();
        if(calendlyCalendar != null){
            existingVetRequest.setCalendlyCalendar(calendlyCalendar);
        }

        String payment = newVetDetails.getPayment();
        if(payment != null){
            existingVetRequest.setPayment(payment);
        }

        VetRequest updatedVetRequest = vetRepository.save(existingVetRequest);

        return convertToUpdateVetDTO(updatedVetRequest);
    }

    private UpdateVetDTO convertToUpdateVetDTO(VetRequest vetRequest) {
        UpdateVetDTO dto = new UpdateVetDTO();
        dto.setName(vetRequest.getName());
        dto.setPassword(vetRequest.getPassword());
        dto.setEmail(vetRequest.getEmail());
        dto.setPhoneNumber(vetRequest.getPhoneNumber());
        dto.setRole(vetRequest.getRole());
        dto.setStatus(vetRequest.getStatus());
        dto.setIsEmergencyVet(vetRequest.getIsEmergencyVet());
        dto.setDistrict(vetRequest.getDistrict());
        dto.setCalendlyCalendar(vetRequest.getCalendlyCalendar());
        dto.setCalendlyEmail(vetRequest.getCalendlyEmail());
        dto.setPayment(vetRequest.getPayment());

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

    public void validateStatus(String status, String errorMessage){
        if (!"enabled".equalsIgnoreCase(status) && !"disabled".equalsIgnoreCase(status)){
            throw new BadRequestException(errorMessage + " Received value: " + status + ", expected: enabled or disabled");
        }
    }
}
