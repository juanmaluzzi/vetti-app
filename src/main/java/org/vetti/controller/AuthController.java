package org.vetti.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.vetti.model.UpdateUserDTO;
import org.vetti.model.User;
import org.vetti.response.LoginResponse;
import org.vetti.service.UserService;


@RestController
@RequestMapping("/user")
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<User> registerUser(@RequestBody User user) {
        User registeredUser = userService.registerUser(user);
        return ResponseEntity.status(201).build();
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> loginUser(@RequestBody User loginUser) {
        return userService.loginUser(loginUser.getEmail(), loginUser.getPassword());
    }

    @PostMapping("/updateUser/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody UpdateUserDTO newUserDetails){

        UpdateUserDTO updateUser = userService.updateUser(id, newUserDetails);

        return ResponseEntity.status(200).build();
    }
}
