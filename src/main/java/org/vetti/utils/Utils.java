package org.vetti.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.vetti.exceptions.BadRequestException;
import org.vetti.exceptions.NotFoundException;
import org.vetti.model.UpdateUserDTO;
import org.vetti.model.User;
import org.vetti.repository.UserRepository;
import org.vetti.response.LoginResponse;

import java.util.Optional;


@Component
public class Utils {

    private final UserRepository userRepository;

    @Autowired
    private final PasswordEncoder passwordEncoder;

    private static final String INVALID_EMAIL = "invalid or empty email.";
    private static final String INVALID_NAME_OR_USERNAME = "invalid or empty name.";
    private static final String INVALID_PASSWORD = "password cannot be empty.";
    private static final String INVALID_PHONENUMBER = "phoneNumber is invalid or empty.";
    private static final String INVALID_ROLE = "role is invalid or empty, field value must be 0, 1 or 2.";
    private static final String EMAIL_ALREADY_EXISTS = "Email already registered.";

    @Autowired
    public Utils(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }


    public void validateUserRegister(User user){

        validateEmail(user.getEmail(), INVALID_EMAIL);
        validateNameAndUsername(user.getName(), INVALID_NAME_OR_USERNAME);
        validateNameAndUsername(user.getLastName(), INVALID_NAME_OR_USERNAME);
        validateNotEmpty(user.getPassword(), INVALID_PASSWORD);
        validatePhoneNumber(user.getPhoneNumber(), INVALID_PHONENUMBER);
        validateRole(user.getRole(), INVALID_ROLE);
        if (findUserByEmail(user.getEmail())) {
            throw new BadRequestException(EMAIL_ALREADY_EXISTS);
        }

    }

    public ResponseEntity<LoginResponse> validateUserCredentials(String email, String password){

        User user = userRepository.findUserByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found with email: " + email));

        if (passwordEncoder.matches(password, user.getPassword())) {
            String role = user.getRole();
            LoginResponse response = new LoginResponse("Success", HttpStatus.OK.value(), role);
            return ResponseEntity.ok(response);
        } else {
            LoginResponse response = new LoginResponse("invalid credentials, please check your email or password.", HttpStatus.UNAUTHORIZED.value());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }

    public UpdateUserDTO updateUser(Long id, UpdateUserDTO newUserDetails){

        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + id));

        if (newUserDetails.getName() != null) {
            validateNameAndUsername(newUserDetails.getName(), INVALID_NAME_OR_USERNAME);
            existingUser.setName(newUserDetails.getName());
        }

        if (newUserDetails.getLastName() != null) {
            validateNameAndUsername(newUserDetails.getLastName(), INVALID_NAME_OR_USERNAME);
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

    private void validateNameAndUsername(String value, String errorMessage) {
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
}
