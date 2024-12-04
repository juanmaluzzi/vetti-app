package org.vetti.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.vetti.exceptions.BadRequestException;
import org.vetti.model.request.VetRequest;
import org.vetti.repository.VetRepository;

import java.util.Map;
import java.util.Optional;

@Service
public class MercadoPagoService {

    private final VetRepository vetRepository;
    private final RestTemplate restTemplate;

    @Value("${mercadopago.token}")
    private String mercadoPagoToken;

    public MercadoPagoService(VetRepository vetRepository, RestTemplate restTemplate) {
        this.vetRepository = vetRepository;
        this.restTemplate = restTemplate;
    }


        public void processPaymentStatus(String preApprovalId, Long vetId) {
        String apiUrl = "https://api.mercadopago.com/v1/payments/" + preApprovalId;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + mercadoPagoToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(apiUrl, HttpMethod.GET, entity, Map.class);

            Map<String, Object> payload = response.getBody();
            if (payload == null || !payload.containsKey("status")) {
                throw new BadRequestException("No se encontró el estado del pago");
            }

            String status = (String) payload.get("status");

            if ("approved".equals(status)) {
                updateVetPaymentStatus(vetId, "paid");
            } else {
                throw new BadRequestException("El pago no está aprobado: " + status);
            }

        } catch (HttpClientErrorException e) {
            throw new BadRequestException("Error al consultar el pago: " + e.getMessage());
        }
    }

    private void updateVetPaymentStatus(Long vetId, String paymentStatus) {
        Optional<VetRequest> vet = vetRepository.findById(vetId);
        if (vet.isPresent()) {
            VetRequest vetEntity = vet.get();
            vetEntity.setPayment(paymentStatus);
            vetRepository.save(vetEntity);
        } else {
            throw new BadRequestException("No se encontró la veterinaria con ID: " + vetId);
        }
    }
}
