package org.vetti.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.vetti.model.UpdateUserDTO;
import org.vetti.model.User;
import org.vetti.repository.UserRepository;
import org.vetti.response.LoginResponse;
import org.vetti.utils.Utils;

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

        utils.validateUserRegister(user);
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        return userRepository.save(user);
    }

    public ResponseEntity<LoginResponse> loginUser(String email, String password) {

        return utils.validateUserCredentials(email, password);
    }

    public UpdateUserDTO updateUser(Long id, UpdateUserDTO newUserDetails){

       return utils.updateUser(id, newUserDetails);

    }

}
