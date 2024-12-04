package org.vetti.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.vetti.exceptions.BadRequestException;
import org.vetti.exceptions.NotFoundException;
import org.vetti.model.dto.UpdateVetDTO;
import org.vetti.model.request.VetRequest;
import org.vetti.repository.VetRepository;
import org.vetti.model.response.LoginVetResponse;
import org.vetti.model.response.SearchVetResponse;
import org.vetti.utils.VetUtils;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    public void vetRegister(VetRequest vetRequest){
        vetUtils.validateVetRegister(vetRequest);
        vetRequest.setPassword(passwordEncoder.encode(vetRequest.getPassword()));

        if (vetRequest.getEmail() != null) {
            vetRequest.setEmail(vetRequest.getEmail().toLowerCase());
        }

        vetRepository.save(vetRequest);
    }

    public ResponseEntity<LoginVetResponse> loginVet(String email, String password) {

        VetRequest vetRequest = vetRepository.findVetByEmail(email.toLowerCase())
                .orElseThrow(() -> new NotFoundException("User not found with email: " + email));

        if (passwordEncoder.matches(password, vetRequest.getPassword())) {
            String role = vetRequest.getRole();
            LoginVetResponse response = new LoginVetResponse("Usuario autenticado correctamente.", HttpStatus.OK.value(), role, vetRequest.getId(), vetRequest.getStatus(), vetRequest.getPayment());
            return ResponseEntity.ok(response);
        } else {
            LoginVetResponse response = new LoginVetResponse("Credenciales invalidas, por favor revise su correo o contraseña.", HttpStatus.UNAUTHORIZED.value());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }

    public UpdateVetDTO updateVet(Long id, UpdateVetDTO newVetDetails){
        return vetUtils.updateVet(id, newVetDetails);
    }

    public ResponseEntity<SearchVetResponse> getVetByEmail(String email){

        VetRequest vetRequest = vetRepository.findVetByEmail(email)
                .orElseThrow(() -> new NotFoundException("Vet not found with email: " + email));

        SearchVetResponse response = new SearchVetResponse(vetRequest.getId(), HttpStatus.OK.value(),"Success", vetRequest.getEmail(), vetRequest.getName(), vetRequest.getAddress(), vetRequest.getDistrict(), vetRequest.getPhoneNumber(), vetRequest.getCuit(), vetRequest.getRole(), vetRequest.getStatus(), vetRequest.getIsEmergencyVet(), vetRequest.getCalendlyEmail(), vetRequest.getCalendlyCalendar(), vetRequest.getPayment());

         return ResponseEntity.ok(response);
    }

    public ResponseEntity<SearchVetResponse> getVetById(Long id){

        VetRequest vetRequest = vetRepository.findVetById(id)
                .orElseThrow(() -> new NotFoundException("Vet not found with id: " + id));

        SearchVetResponse response = new SearchVetResponse(vetRequest.getId(), HttpStatus.OK.value(),"Success", vetRequest.getEmail(), vetRequest.getName(), vetRequest.getAddress(), vetRequest.getDistrict(), vetRequest.getPhoneNumber(), vetRequest.getCuit(), vetRequest.getRole(), vetRequest.getStatus(), vetRequest.getIsEmergencyVet(), vetRequest.getCalendlyEmail(), vetRequest.getCalendlyCalendar(), vetRequest.getPayment());

        return ResponseEntity.ok(response);
    }

    public ResponseEntity<List<SearchVetResponse>> getAllVets(){

        List<VetRequest> vetRequests = vetRepository.findByStatus("enabled");

        if (vetRequests.isEmpty()) {
            throw new NotFoundException("No se encontraron veterinarias.");
        }

        List<SearchVetResponse> responseList = vetRequests.stream()
                .map(vetRequest -> new SearchVetResponse(
                        vetRequest.getId(),
                        HttpStatus.OK.value(),
                        "Success",
                        vetRequest.getEmail(),
                        vetRequest.getName(),
                        vetRequest.getAddress(),
                        vetRequest.getDistrict(),
                        vetRequest.getPhoneNumber(),
                        vetRequest.getCuit(),
                        vetRequest.getRole(),
                        vetRequest.getStatus(),
                        vetRequest.getIsEmergencyVet(),
                        vetRequest.getCalendlyEmail(),
                        vetRequest.getCalendlyCalendar(),
                        vetRequest.getPayment()
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(responseList);
    }
}
