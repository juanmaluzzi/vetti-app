package org.vetti.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.vetti.exceptions.BadRequestException;
import org.vetti.model.request.UserRequest;
import org.vetti.model.request.VetRequest;
import org.vetti.repository.UserRepository;
import org.vetti.repository.VetRepository;

import javax.mail.MessagingException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class PasswordService {

    private final UserRepository userRepository;
    private final VetRepository vetRepository;
    private final EmailService emailService;
    private final BCryptPasswordEncoder passwordEncoder;

    public PasswordService(UserRepository userRepository, EmailService emailService, BCryptPasswordEncoder passwordEncoder, VetRepository vetRepository) {
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
        this.vetRepository = vetRepository;
    }

    public void requestPasswordReset(String email) throws MessagingException, IOException {
        UserRequest user = userRepository.findUserByEmail(email).orElse(null);

        if (user != null) {
            String resetCode = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
            user.setPasswordResetCode(resetCode);
            user.setPasswordResetExpiry(LocalDateTime.now().plusMinutes(15));
            userRepository.save(user);

            emailService.sendPasswordResetEmail(email, resetCode);
            return;
        }

        VetRequest vet = vetRepository.findVetByEmail(email).orElse(null);

        if (vet != null) {
            String resetCode = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
            vet.setPasswordResetCode(resetCode);
            vet.setPasswordResetExpiry(LocalDateTime.now().plusMinutes(15));
            vetRepository.save(vet);

            emailService.sendPasswordResetEmail(email, resetCode);
            return;
        }

        throw new BadRequestException("No se encontró un usuario ni una veterinaria con el email: " + email);
    }


    public void resetPassword(String email, String resetCode, String newPassword) {
        UserRequest user = userRepository.findUserByEmail(email).orElse(null);

        if (user != null) {
            validateResetCode(user.getPasswordResetCode(), user.getPasswordResetExpiry(), resetCode);
            user.setPassword(passwordEncoder.encode(newPassword));
            user.setPasswordResetCode(null);
            user.setPasswordResetExpiry(null);
            userRepository.save(user);
            return;
        }

        VetRequest vet = vetRepository.findVetByEmail(email).orElse(null);

        if (vet != null) {
            validateResetCode(vet.getPasswordResetCode(), vet.getPasswordResetExpiry(), resetCode);
            vet.setPassword(passwordEncoder.encode(newPassword));
            vet.setPasswordResetCode(null);
            vet.setPasswordResetExpiry(null);
            vetRepository.save(vet);
            return;
        }

        throw new BadRequestException("No se encontró un usuario ni una veterinaria con el email: " + email);
    }


    private void validateResetCode(String storedCode, LocalDateTime expiry, String providedCode) {
        if (!providedCode.equals(storedCode)) {
            throw new BadRequestException("El código de recuperación no es válido.");
        }

        if (expiry == null || expiry.isBefore(LocalDateTime.now())) {
            throw new BadRequestException("El código de recuperación ha expirado.");
        }
    }

}
