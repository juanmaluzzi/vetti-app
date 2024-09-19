package org.vetti.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.vetti.exceptions.BadRequestException;
import org.vetti.exceptions.NotFoundException;
import org.vetti.model.UpdateUserDTO;
import org.vetti.model.User;
import org.vetti.model.Vet;
import org.vetti.repository.UserRepository;
import org.vetti.repository.VetRepository;


@Component
public class Utils {

    private final UserRepository userRepository;

    private final VetRepository vetRepository;

    @Autowired
    private final PasswordEncoder passwordEncoder;

    private static final String INVALID_EMAIL = "invalid or empty email.";
    private static final String INVALID_STRING = "invalid or empty name.";
    private static final String INVALID_PASSWORD = "password cannot be empty.";
    private static final String INVALID_CUIT = "invalid or empty cuit.";
    private static final String INVALID_PHONENUMBER = "phoneNumber is invalid or empty.";
    private static final String INVALID_ROLE = "role is invalid or empty, field value must be 0, 1 or 2.";
    private static final String EMAIL_ALREADY_EXISTS = "Email already registered.";

    @Autowired
    public Utils(UserRepository userRepository, VetRepository vetRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.vetRepository = vetRepository;
        this.passwordEncoder = passwordEncoder;
    }

  
    public void validateUserRegister(User user){

        validateEmail(user.getEmail(), INVALID_EMAIL);
        validateString(user.getName(), INVALID_STRING);
        validateString(user.getLastName(), INVALID_STRING);
        validateNotEmpty(user.getPassword(), INVALID_PASSWORD);
        validatePhoneNumber(user.getPhoneNumber(), INVALID_PHONENUMBER);
        validateRole(user.getRole(), INVALID_ROLE);
        if (findUserByEmail(user.getEmail())) {
            throw new BadRequestException(EMAIL_ALREADY_EXISTS);
        }

    }

    public void validateVetRegister(Vet vet){
        validateEmail(vet.getEmail(), INVALID_EMAIL);
        validateString(vet.getName(), INVALID_STRING);
        validateString(vet.getAddress(), INVALID_STRING);
        validateCuit(vet.getCuit(), INVALID_CUIT);
        validateNotEmpty(vet.getPassword(), INVALID_PASSWORD);
        validatePhoneNumber(vet.getPhoneNumber(), INVALID_PHONENUMBER);
        validateRole(vet.getRole(), INVALID_ROLE);
        if (findUserByEmail(vet.getEmail())) {
            throw new BadRequestException(EMAIL_ALREADY_EXISTS);
        }
    }


    public UpdateUserDTO updateUser(Long id, UpdateUserDTO newUserDetails){

        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + id));

        if (newUserDetails.getName() != null) {
            validateString(newUserDetails.getName(), INVALID_STRING);
            existingUser.setName(newUserDetails.getName());
        }

        if (newUserDetails.getLastName() != null) {
            validateString(newUserDetails.getLastName(), INVALID_STRING);
            existingUser.setLastName(newUserDetails.getLastName());
        }

        if (newUserDetails.getEmail() != null) {
            validateEmail(newUserDetails.getEmail(), INVALID_EMAIL);

            if (findUserByEmail(newUserDetails.getEmail())) {
                throw new BadRequestException(EMAIL_ALREADY_EXISTS);
            }
            existingUser.setEmail(newUserDetails.getEmail());
        }

        if (newUserDetails.getRole() != null) {
            validateRole(newUserDetails.getRole(), INVALID_ROLE);
            existingUser.setRole(newUserDetails.getRole());
        }

        if (newUserDetails.getPhoneNumber() != null) {
            validatePhoneNumber(newUserDetails.getPhoneNumber(), INVALID_PHONENUMBER);
            existingUser.setPhoneNumber(newUserDetails.getPhoneNumber());
        }

        if (newUserDetails.getPassword() != null) {
            validateNotEmpty(newUserDetails.getPassword(), INVALID_PASSWORD);
            existingUser.setPassword(passwordEncoder.encode(newUserDetails.getPassword()));
        }
        User updatedUser = userRepository.save(existingUser);

        return convertToUpdateUserDTO(updatedUser);
    }

    private UpdateUserDTO convertToUpdateUserDTO(User user) {
        UpdateUserDTO dto = new UpdateUserDTO();
        dto.setName(user.getName());
        dto.setLastName(user.getLastName());
        dto.setPassword(user.getPassword());
        dto.setEmail(user.getEmail());
        dto.setPhoneNumber(user.getPhoneNumber());
        dto.setRole(user.getRole());

        return dto;
    }

    public boolean findUserByEmail(String email) {
        return userRepository.findUserByEmail(email).isPresent();
    }

    private void validateString(String value, String errorMessage) {
        if (value == null || value.trim().isEmpty() || !value.matches("^[a-zA-Z\\s]+$")) throw new BadRequestException(errorMessage + " Received value: " +  value);
    }

    private void validatePhoneNumber(String value, String errorMessage){
        if (value == null || value.trim().isEmpty() || !value.matches("^\\d{8,14}$")) throw new BadRequestException(errorMessage + " Received value: " + value);
    }

    private void validateEmail(String value, String errorMessage){
        if (value == null || value.trim().isEmpty() || !value.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")) throw new BadRequestException(errorMessage + " Received value: " + value);
    }

    private void validateNotEmpty(String value, String errorMessage){
        if (value == null || value.trim().isEmpty()) throw new BadRequestException(errorMessage);
    }

    private void validateRole(String value, String errorMessage){
        if (value != null) {
            if (!value.matches(("^[0-2]$"))) throw new BadRequestException(errorMessage + " Received value: " + value);
        }
    }

    private void validateCuit(String value, String errorMessage){
        if (value == null || value.trim().isEmpty() || !value.matches("^(20|23|24|27|30|33|34)\\d{8}\\d$")) throw new BadRequestException(errorMessage + " Received value: " + value);
    }
}
