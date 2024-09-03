package org.vetti.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.vetti.exceptions.BadRequestException;
import org.vetti.model.User;
import org.vetti.repository.UserRepository;
import org.vetti.response.LoginResponse;
import org.vetti.utils.Utils;
import org.vetti.utils.Utils.*;

import java.util.Optional;

import static com.nimbusds.oauth2.sdk.util.StringUtils.isNumeric;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private Utils utils;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, Utils utils) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.utils = utils;
    }

    public User registerUser(User user) {

        utils.validateUser(user);
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        return userRepository.save(user);
    }

    public ResponseEntity<LoginResponse> loginUser(String email, String password) {

        return validateCredentials(email, password);
    }

    private ResponseEntity<LoginResponse> validateCredentials(String email, String password){

        Optional<User> user = userRepository.findByEmail(email);

        if (user.isPresent() && passwordEncoder.matches(password, user.get().getPassword())) {
            LoginResponse response = new LoginResponse("Success", HttpStatus.OK.value());
            return ResponseEntity.ok(response);
        } else {
            LoginResponse response = new LoginResponse("invalid credentials, please check your email or password.", HttpStatus.UNAUTHORIZED.value());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }
}
