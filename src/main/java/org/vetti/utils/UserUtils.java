package org.vetti.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.vetti.exceptions.BadRequestException;
import org.vetti.exceptions.NotFoundException;
import org.vetti.model.User;
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

    public void validateUserRegister(User user){

        utils.validateEmail(user.getEmail(), INVALID_EMAIL);
        utils.validateString(user.getName(), INVALID_STRING);
        utils.validateString(user.getLastName(), INVALID_STRING);
        utils.validateNotEmpty(user.getPassword(), INVALID_PASSWORD);
        utils.validatePhoneNumber(user.getPhoneNumber(), INVALID_PHONENUMBER);
        utils.validateRole(user.getRole(), INVALID_ROLE);
        utils.validateDni(user.getDni(), INVALID_DNI);
        if (findUserByEmail(user.getEmail())) {
            throw new BadRequestException(EMAIL_ALREADY_EXISTS);
        }
        if (findUserByDni(user.getDni())) {
            throw new BadRequestException(DNI_ALREADY_EXISTS);
        }

    }

    public UpdateUserDTO updateUser(Long id, UpdateUserDTO newUserDetails){

        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + id));

        String name = newUserDetails.getName();
        if (name != null && !name.trim().isEmpty()) {
            utils.validateString(newUserDetails.getName(), INVALID_STRING);
            existingUser.setName(newUserDetails.getName());
        }

        String lastName = newUserDetails.getLastName();
        if (lastName != null && !lastName.trim().isEmpty()) {
            utils.validateString(newUserDetails.getLastName(), INVALID_STRING);
            existingUser.setLastName(newUserDetails.getLastName());
        }

        String email = newUserDetails.getEmail();
        if (email != null && !email.trim().isEmpty()) {
            utils.validateEmail(newUserDetails.getEmail(), INVALID_EMAIL);

            if (findUserByEmail(newUserDetails.getEmail())) {
                throw new BadRequestException(EMAIL_ALREADY_EXISTS);
            }
            existingUser.setEmail(newUserDetails.getEmail());
        }

        String role = newUserDetails.getRole();
        if (role != null && !role.trim().isEmpty()) {
            utils.validateRole(newUserDetails.getRole(), INVALID_ROLE);
            existingUser.setRole(newUserDetails.getRole());
        }

        String phoneNumber = newUserDetails.getPhoneNumber();
        if (phoneNumber != null && !phoneNumber.trim().isEmpty()) {
            utils.validatePhoneNumber(newUserDetails.getPhoneNumber(), INVALID_PHONENUMBER);
            existingUser.setPhoneNumber(newUserDetails.getPhoneNumber());
        }

        String password = newUserDetails.getPassword();
        if (password != null && !password.trim().isEmpty()) {
            utils.validateNotEmpty(newUserDetails.getPassword(), INVALID_PASSWORD);
            existingUser.setPassword(passwordEncoder.encode(newUserDetails.getPassword()));
        }

        String address = newUserDetails.getAddress();
        if (address != null && !address.trim().isEmpty()) {
            utils.validateNotEmpty(newUserDetails.getAddress(), INVALID_ADDRESS);
            existingUser.setAddress(newUserDetails.getAddress());
        }

        String dni = newUserDetails.getDni();
        if (dni != null && !dni.trim().isEmpty()) {
            utils.validateDni(newUserDetails.getDni(), INVALID_DNI);
            existingUser.setDni(newUserDetails.getDni());
        }

        String district = newUserDetails.getDistrict();
        if (district != null && !district.trim().isEmpty()) {
            utils.validateNotEmpty(newUserDetails.getDistrict(), INVALID_DISTRICT);
            existingUser.setDistrict(newUserDetails.getDistrict());
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

    private boolean findUserByEmail(String email) {
        return userRepository.findUserByEmail(email).isPresent();
    }

    private boolean findUserByDni(String dni) {
        return userRepository.findUserByDni(dni).isPresent();
    }

}
