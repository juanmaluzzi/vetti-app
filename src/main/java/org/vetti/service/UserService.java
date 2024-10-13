package org.vetti.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.vetti.exceptions.NotFoundException;
import org.vetti.model.dto.PetDTO;
import org.vetti.model.dto.UpdateUserDTO;
import org.vetti.model.User;
import org.vetti.repository.UserRepository;
import org.vetti.response.LoginResponse;
import org.vetti.response.SearchUserResponse;
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

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, UserUtils userUtils) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userUtils = userUtils;
    }

    public User registerUser(User user) {

        userUtils.validateUserRegister(user);
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        if (user.getPets() != null) {
            user.getPets().forEach(pet -> pet.setUser(user));
        }

        return userRepository.save(user);
    }

    public ResponseEntity<LoginResponse> loginUser(String email, String password) {
            User user = userRepository.findUserByEmail(email)
                    .orElseThrow(() -> new NotFoundException("User not found with email: " + email));

            if (passwordEncoder.matches(password, user.getPassword())) {
                String role = user.getRole();
                LoginResponse response = new LoginResponse("Success", HttpStatus.OK.value(), role, user.getId());
                return ResponseEntity.ok(response);
            } else {
                LoginResponse response = new LoginResponse("invalid credentials, please check your email or password.", HttpStatus.UNAUTHORIZED.value());
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
    }

    public UpdateUserDTO updateUser(Long id, UpdateUserDTO newUserDetails){

       return userUtils.updateUser(id, newUserDetails);

    }


    public ResponseEntity<SearchUserResponse> getUserByEmail(String email){

        User user = userRepository.findUserByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found with email: " + email));

        List<PetDTO> petDTOs = user.getPets().stream()
                .map(pet -> new PetDTO(pet.getId(), pet.getName(), pet.getType()))
                .collect(Collectors.toList());

        SearchUserResponse response = new SearchUserResponse(user.getId(), HttpStatus.OK.value(), "Success", user.getEmail(), user.getName(), user.getLastName(), user.getPhoneNumber(), user.getRole(), user.getDni(), user.getAddress(), user.getDistrict(), petDTOs);

        return ResponseEntity.ok(response);
    }

    public ResponseEntity<SearchUserResponse> getUserById(Long id){

        User user = userRepository.findUserById(id)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + id));

        List<PetDTO> petDTOs = user.getPets().stream()
                .map(pet -> new PetDTO(pet.getId(), pet.getName(), pet.getType()))
                .collect(Collectors.toList());

        SearchUserResponse response = new SearchUserResponse(user.getId(), HttpStatus.OK.value(), "Success", user.getEmail(), user.getName(), user.getLastName(), user.getPhoneNumber(), user.getRole(), user.getDni(), user.getAddress(), user.getDistrict(), petDTOs);

        return ResponseEntity.ok(response);
    }

}
