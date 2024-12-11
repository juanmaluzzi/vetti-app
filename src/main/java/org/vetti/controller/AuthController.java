package org.vetti.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.vetti.service.AuthService;

import java.util.Map;

@Slf4j
@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/getToken")
    public ResponseEntity<Map<String, String>> getAuthToken(@RequestHeader("Authorization") String apiKey) {
        log.info("Empezando a hacer el request");

        if (!authService.isValidApiKey(apiKey)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Unauthorized: Invalid API Key"));
        }

        try {
            Map<String, String> tokenResponse = authService.fetchAuthToken();
            return ResponseEntity.ok(tokenResponse);
        } catch (Exception e) {
            log.error("Error al obtener el token de autenticación", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error retrieving access token: " + e.getMessage()));
        }
    }
}
