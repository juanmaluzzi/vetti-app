package org.vetti.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.vetti.exceptions.NotFoundException;
import org.vetti.model.UpdateUserDTO;
import org.vetti.model.User;
import org.vetti.repository.UserRepository;
import org.vetti.response.LoginResponse;
import org.vetti.utils.Utils;

@Service
public class UserService {

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final PasswordEncoder passwordEncoder;

    @Autowired
    private final Utils utils;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, Utils utils) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.utils = utils;
    }

    public User registerUser(User user) {

        utils.validateUserRegister(user);
        user.setPassword(passwordEncoder.encode(user.getPassword()));

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

       return utils.updateUser(id, newUserDetails);

    }

    public User getUserByEmail(String email){

        return userRepository.findUserByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found with email: " + email));
    }

}
