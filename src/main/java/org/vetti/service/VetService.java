package org.vetti.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.vetti.exceptions.NotFoundException;
import org.vetti.model.dto.UpdateVetDTO;
import org.vetti.model.Vet;
import org.vetti.repository.VetRepository;
import org.vetti.response.LoginResponse;
import org.vetti.response.SearchVetResponse;
import org.vetti.utils.VetUtils;

@Service
public class VetService {

    @Autowired
    private final VetRepository vetRepository;

    @Autowired
    private final PasswordEncoder passwordEncoder;

    @Autowired
    private final VetUtils vetUtils;

    @Autowired
    public VetService(VetRepository vetRepository, PasswordEncoder passwordEncoder, VetUtils vetUtils){
        this.vetRepository = vetRepository;
        this.passwordEncoder = passwordEncoder;
        this.vetUtils = vetUtils;
    }

    public Vet vetRegister(Vet vet){
        vetUtils.validateVetRegister(vet);
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
        return vetUtils.updateVet(id, newVetDetails);
    }

    public ResponseEntity<SearchVetResponse> getVetByEmail(String email){

        Vet vet = vetRepository.findVetByEmail(email)
                .orElseThrow(() -> new NotFoundException("Vet not found with email: " + email));

        SearchVetResponse response = new SearchVetResponse(vet.getId(), HttpStatus.OK.value(),"Success", vet.getEmail(), vet.getName(), vet.getAddress(), vet.getPhoneNumber(), vet.getCuit(), vet.getRole(), vet.getIsEmergencyVet(), vet.getCalendlyEmail(), vet.getCalendlyCalendar());

         return ResponseEntity.ok(response);
    }

    public Vet updateCalendlyToken(Long vetId, String calendlyToken) {
        Vet vet = vetRepository.findById(vetId)
                .orElseThrow(() -> new NotFoundException("Vet not found with ID: " + vetId));

        vet.setCalendlyToken(calendlyToken);
        return vetRepository.save(vet);
    }

    public ResponseEntity<SearchVetResponse> getVetById(Long id){

        Vet vet = vetRepository.findVetById(id)
                .orElseThrow(() -> new NotFoundException("Vet not found with id: " + id));

        SearchVetResponse response = new SearchVetResponse(vet.getId(), HttpStatus.OK.value(),"Success", vet.getEmail(), vet.getName(), vet.getAddress(), vet.getPhoneNumber(), vet.getCuit(), vet.getRole(), vet.getIsEmergencyVet(), vet.getCalendlyEmail(), vet.getCalendlyCalendar());

        return ResponseEntity.ok(response);
    }
}
