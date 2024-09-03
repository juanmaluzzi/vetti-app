package org.vetti.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.vetti.exceptions.BadRequestException;
import org.vetti.model.User;
import org.vetti.repository.UserRepository;

@Component
public class Utils {

    private final UserRepository userRepository;

    @Autowired
    public Utils(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    public void validateUser(User user){

        validateNotEmpty(user.getEmail(), "email cannot be empty.");
        validateNotEmpty(user.getName(), "name cannot be empty.");
        validateNotEmpty(user.getLastName(), "last name cannot be empty");
        validateNotEmpty(user.getPassword(), "password cannot be empty");
        validatePhoneNumber(user.getPhoneNumber(), "phone number is invalid or empty.");

        if (findByEmail(user.getEmail())) {
            throw new BadRequestException("Email already registered.");
        }
    }


    public boolean findByEmail(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    private void validateNotEmpty(String value, String errorMessage) {
        if (value == null || value.trim().isEmpty()) {
            throw new BadRequestException(errorMessage);
        }
    }

    private void validatePhoneNumber(String value, String errorMessage){
        if (value == null || !value.matches("^\\d{8,14}$")){
            throw new BadRequestException(errorMessage);
        }
    }
}
