package org.vetti.utils;

import org.springframework.stereotype.Component;
import org.vetti.exceptions.BadRequestException;

@Component
public class Utils {

    public static final String INVALID_EMAIL = "invalid or empty email.";
    public static final String INVALID_STRING = "invalid or empty name.";
    public static final String INVALID_PASSWORD = "password cannot be empty.";
    public static final String INVALID_CUIT = "invalid or empty cuit.";
    public static final String INVALID_PHONENUMBER = "phoneNumber is invalid or empty.";
    public static final String INVALID_ROLE = "role is invalid or empty, field value must be 0, 1 or 2.";
    public static final String EMAIL_ALREADY_EXISTS = "Email already registered.";
    public static final String CUIT_ALREADY_EXISTS = "Cuit already registered.";
    public static final String INVALID_ADDRESS = "address is invalid or empty";
    public static final String INVALID_DNI = "DNI is invalid or empty";
    public static final String INVALID_DISTRICT = "District is invalid or empty";
    public static final String DNI_ALREADY_EXISTS = "DNI already registered.";
    public static final String INVALID_EMERGENCY_VET = "Emergency vet field is invalid";
    public static final String INVALID_STATUS = "Status field is invalid or empty";
    public static final String INVALID_STATUSFIELD = "Status is invalid.";

    public void validateString(String value, String errorMessage) {
        if (value == null || value.trim().isEmpty() || !value.matches("^[a-zA-Z\\s]+$")) throw new BadRequestException(errorMessage + " Received value: " +  value);
    }

    public void validatePhoneNumber(String value, String errorMessage){
        if (value == null || value.trim().isEmpty() || !value.matches("^\\d{8,14}$")) throw new BadRequestException(errorMessage + " Received value: " + value);
    }

    public void validateEmail(String value, String errorMessage){
        if (value == null || value.trim().isEmpty() || !value.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")) throw new BadRequestException(errorMessage + " Received value: " + value);
    }

    public void validateNotEmpty(String value, String errorMessage){
        if (value == null || value.trim().isEmpty()) throw new BadRequestException(errorMessage);
    }

    public void validateRole(String value, String errorMessage){
        if (value != null) {
            if (!value.matches(("^[0-2]$"))) throw new BadRequestException(errorMessage + " Received value: " + value);
        }
    }

    public void validateDni(String value, String errorMessage){
        if (value != null) {
            if (!value.matches(("^\\d{8}$"))) throw new BadRequestException(errorMessage + " Received value: " + value);
        }
    }

    public void validateBoolean(Boolean value, String errorMessage) {
        if (value == null) {
            throw new BadRequestException(errorMessage);
        }
    }

    public void validateStatus(String status){
        if (!"active".equalsIgnoreCase(status) && !"canceled".equalsIgnoreCase(status)){
            throw new BadRequestException("Received value: " + status + " || expected: active or canceled");
        }
    }

}
