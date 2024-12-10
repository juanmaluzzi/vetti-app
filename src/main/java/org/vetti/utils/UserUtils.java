package org.vetti.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.vetti.exceptions.BadRequestException;
import org.vetti.exceptions.NotFoundException;
import org.vetti.model.request.UserRequest;
import org.vetti.model.dto.UpdateUserDTO;
import org.vetti.repository.UserRepository;

import static org.vetti.utils.Utils.*;

@Slf4j
@Component
public class UserUtils {

    private final UserRepository userRepository;

    private final Utils utils;

    @Autowired
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserUtils(Utils utils, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.utils = utils;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void validateUserRegister(UserRequest userRequest){

        utils.validateEmail(userRequest.getEmail(), INVALID_EMAIL);
        utils.validateString(userRequest.getName(), INVALID_STRING);
        utils.validateString(userRequest.getLastName(), INVALID_STRING);
        utils.validateNotEmpty(userRequest.getPassword(), INVALID_PASSWORD);
        utils.validatePhoneNumber(userRequest.getPhoneNumber(), INVALID_PHONENUMBER);
        utils.validateRole(userRequest.getRole(), INVALID_ROLE);
        utils.validateDni(userRequest.getDni(), INVALID_DNI);
        if (findUserByEmail(userRequest.getEmail())) {
            throw new BadRequestException(EMAIL_ALREADY_EXISTS);
        }
        if (findUserByDni(userRequest.getDni())) {
            throw new BadRequestException(DNI_ALREADY_EXISTS);
        }

    }

    public UpdateUserDTO updateUser(Long id, UpdateUserDTO newUserDetails){

        UserRequest existingUserRequest = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + id));

        String name = newUserDetails.getName();
        if (name != null && !name.trim().isEmpty()) {
            utils.validateString(name, INVALID_STRING);
            existingUserRequest.setName(name);
        }

        String lastName = newUserDetails.getLastName();
        if (lastName != null && !lastName.trim().isEmpty()) {
            utils.validateString(lastName, INVALID_STRING);
            existingUserRequest.setLastName(lastName);
        }

        String email = newUserDetails.getEmail();
        if (email != null && !email.trim().isEmpty()) {
            utils.validateEmail(email, INVALID_EMAIL);

            if (findUserByEmail(email)) {
                throw new BadRequestException(EMAIL_ALREADY_EXISTS);
            }
            existingUserRequest.setEmail(email);
        }

        String role = newUserDetails.getRole();
        if (role != null && !role.trim().isEmpty()) {
            utils.validateRole(role, INVALID_ROLE);
            existingUserRequest.setRole(role);
        }

        String phoneNumber = newUserDetails.getPhoneNumber();
        if (phoneNumber != null && !phoneNumber.trim().isEmpty()) {
            utils.validatePhoneNumber(phoneNumber, INVALID_PHONENUMBER);
            existingUserRequest.setPhoneNumber(phoneNumber);
        }

        String password = newUserDetails.getPassword();
        if (password != null && !password.trim().isEmpty()) {
            utils.validateNotEmpty(password, INVALID_PASSWORD);
            existingUserRequest.setPassword(passwordEncoder.encode(password));
        }

        String address = newUserDetails.getAddress();
        if (address != null && !address.trim().isEmpty()) {
            utils.validateNotEmpty(address, INVALID_ADDRESS);
            existingUserRequest.setAddress(address);
        }

        String dni = newUserDetails.getDni();
        if (dni != null && !dni.trim().isEmpty()) {
            utils.validateDni(dni, INVALID_DNI);
            existingUserRequest.setDni(dni);
        }

        String district = newUserDetails.getDistrict();
        if (district != null && !district.trim().isEmpty()) {
            utils.validateNotEmpty(district, INVALID_DISTRICT);
            existingUserRequest.setDistrict(district);
        }

        UserRequest updatedUserRequest = userRepository.save(existingUserRequest);
        return convertToUpdateUserDTO(updatedUserRequest);
    }

    private UpdateUserDTO convertToUpdateUserDTO(UserRequest userRequest) {
        UpdateUserDTO dto = new UpdateUserDTO();
        dto.setName(userRequest.getName());
        dto.setLastName(userRequest.getLastName());
        dto.setPassword(userRequest.getPassword());
        dto.setEmail(userRequest.getEmail());
        dto.setPhoneNumber(userRequest.getPhoneNumber());
        dto.setRole(userRequest.getRole());

        return dto;
    }

    private boolean findUserByEmail(String email) {
        return userRepository.findUserByEmail(email).isPresent();
    }

    private boolean findUserByDni(String dni) {
        return userRepository.findUserByDni(dni).isPresent();
    }

}
