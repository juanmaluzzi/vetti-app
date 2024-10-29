package org.vetti.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.vetti.model.dto.UpdateUserDTO;
import org.vetti.model.User;
import org.vetti.response.LoginResponse;
import org.vetti.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/user")
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
public class UserController {

    @Autowired
    private UserService userService;

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        User registeredUser = userService.registerUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(registeredUser);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> loginUser(@RequestBody User loginUser) {
        logger.info(String.valueOf(loginUser.getEmail()));
        logger.info("llegamos al endpoint");
        return userService.loginUser(loginUser.getEmail(), loginUser.getPassword());
    }

    @PatchMapping("/updateUser/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody UpdateUserDTO newUserDetails){

        UpdateUserDTO updateUser = userService.updateUser(id, newUserDetails);

        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("message", "User updated successfully");
        responseBody.put("userUpdated", updateUser.getEmail());

        return ResponseEntity.status(200).body(responseBody);
    }

    @GetMapping("/searchUserByEmail/{email}")
    public ResponseEntity<?> getUserByEmail(@PathVariable String email){

        return userService.getUserByEmail(email);
    }

    @GetMapping("/searchUserById/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id){

        return userService.getUserById(id);
    }
}
