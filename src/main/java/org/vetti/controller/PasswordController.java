package org.vetti.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.vetti.model.dto.PasswordResetDTO;
import org.vetti.model.dto.PasswordResetRequestDTO;
import org.vetti.service.PasswordService;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/password")
public class PasswordController {

    private final PasswordService passwordService;

    public PasswordController(PasswordService passwordService) {
        this.passwordService = passwordService;
    }

    @PostMapping("/request/email")
    public ResponseEntity<?> requestPasswordReset(@Valid @RequestBody PasswordResetRequestDTO request) {
        passwordService.requestPasswordReset(request.getEmail());
        return ResponseEntity.ok("Se envió un código de recuperación al correo: " + request.getEmail());
    }

    @PostMapping("/reset")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody PasswordResetDTO request) {
        passwordService.resetPassword(request.getEmail(), request.getCode(), request.getNewPassword());
        return ResponseEntity.ok("Contraseña actualizada correctamente.");
    }
}