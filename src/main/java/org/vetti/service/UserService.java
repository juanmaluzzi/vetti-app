package org.vetti.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.vetti.controller.UserController;
import org.vetti.exceptions.NotFoundException;
import org.vetti.model.dto.PetDTO;
import org.vetti.model.dto.UpdateUserDTO;
import org.vetti.model.request.UserRequest;
import org.vetti.model.response.LoginUserResponse;
import org.vetti.repository.UserRepository;
import org.vetti.model.response.SearchUserResponse;
import org.vetti.utils.UserUtils;

import java.util.List;
import java.util.stream.Collectors;


@Service
public class UserService {

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final PasswordEncoder passwordEncoder;

    @Autowired
    private final UserUtils userUtils;

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, UserUtils userUtils) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userUtils = userUtils;
    }

    public UserRequest registerUser(UserRequest userRequest) {

        userUtils.validateUserRegister(userRequest);
        userRequest.setPassword(passwordEncoder.encode(userRequest.getPassword()));

        if (userRequest.getPetRequests() != null) {
            userRequest.getPetRequests().forEach(pet -> pet.setUserRequest(userRequest));
        }

        if (userRequest.getEmail() != null) {
            userRequest.setEmail(userRequest.getEmail().toLowerCase());
        }

        return userRepository.save(userRequest);
    }

    public ResponseEntity<LoginUserResponse> loginUser(String email, String password) {
            UserRequest userRequest = userRepository.findUserByEmail(email.toLowerCase())
                    .orElseThrow(() -> new NotFoundException("User not found with email: " + email));

            if (passwordEncoder.matches(password, userRequest.getPassword())) {
                String role = userRequest.getRole();
                LoginUserResponse response = new LoginUserResponse("Usuario autenticado correctamente.", HttpStatus.OK.value(), role, userRequest.getId());
                return ResponseEntity.ok(response);
            } else {
                LoginUserResponse response = new LoginUserResponse("Credenciales invalidas, por favor revise su correo o contraseña.", HttpStatus.UNAUTHORIZED.value());
                logger.info("Credenciales invalidas.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
    }

    public UpdateUserDTO updateUser(Long id, UpdateUserDTO newUserDetails){

       return userUtils.updateUser(id, newUserDetails);

    }


    public ResponseEntity<SearchUserResponse> getUserByEmail(String email){

        UserRequest userRequest = userRepository.findUserByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found with email: " + email));

        List<PetDTO> petDTOs = userRequest.getPetRequests().stream()
                .map(pet -> new PetDTO(pet.getId(), pet.getName(), pet.getType(), pet.getBirthday()))
                .collect(Collectors.toList());

        SearchUserResponse response = new SearchUserResponse(userRequest.getId(), HttpStatus.OK.value(), "Success", userRequest.getEmail(), userRequest.getName(), userRequest.getLastName(), userRequest.getPhoneNumber(), userRequest.getRole(), userRequest.getDni(), userRequest.getAddress(), userRequest.getDistrict(), petDTOs);

        return ResponseEntity.ok(response);
    }

    public ResponseEntity<SearchUserResponse> getUserById(Long id){

        UserRequest userRequest = userRepository.findUserById(id)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + id));

        List<PetDTO> petDTOs = userRequest.getPetRequests().stream()
                .map(pet -> new PetDTO(pet.getId(), pet.getName(), pet.getType(), pet.getBirthday()))
                .collect(Collectors.toList());

        SearchUserResponse response = new SearchUserResponse(userRequest.getId(), HttpStatus.OK.value(), "Success", userRequest.getEmail(), userRequest.getName(), userRequest.getLastName(), userRequest.getPhoneNumber(), userRequest.getRole(), userRequest.getDni(), userRequest.getAddress(), userRequest.getDistrict(), petDTOs);

        return ResponseEntity.ok(response);
    }

}
