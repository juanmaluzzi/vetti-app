package org.vetti.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.vetti.exceptions.NotFoundException;
import org.vetti.model.UpdateVetDTO;
import org.vetti.model.Vet;
import org.vetti.repository.VetRepository;
import org.vetti.response.LoginResponse;
import org.vetti.utils.Utils;

@Service
public class VetService {

    @Autowired
    private final VetRepository vetRepository;

    @Autowired
    private final PasswordEncoder passwordEncoder;

    @Autowired
    private final Utils utils;

    @Autowired
    public VetService(VetRepository vetRepository, PasswordEncoder passwordEncoder, Utils utils){
        this.vetRepository = vetRepository;
        this.passwordEncoder = passwordEncoder;
        this.utils = utils;
    }

    public Vet VetRegister(Vet vet){
        utils.validateVetRegister(vet);
        vet.setPassword(passwordEncoder.encode(vet.getPassword()));

        return vetRepository.save(vet);
    }

    public ResponseEntity<LoginResponse> loginVet(String email, String password) {

        Vet vet = vetRepository.findVetByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found with email: " + email));

        if (passwordEncoder.matches(password, vet.getPassword())) {
            String role = vet.getRole();
            LoginResponse response = new LoginResponse("Success", HttpStatus.OK.value(), role, vet.getId());
            return ResponseEntity.ok(response);
        } else {
            LoginResponse response = new LoginResponse("invalid credentials, please check your email or password.", HttpStatus.UNAUTHORIZED.value());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }

    public UpdateVetDTO updateVet(Long id, UpdateVetDTO newVetDetails){

        return utils.(id, newVetDetails);

    }
}
