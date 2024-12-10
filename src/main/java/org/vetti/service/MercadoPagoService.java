package org.vetti.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.vetti.exceptions.BadRequestException;
import org.vetti.exceptions.NotFoundException;
import org.vetti.model.request.VetRequest;
import org.vetti.repository.VetRepository;

import javax.mail.MessagingException;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class MercadoPagoService {

    private final VetRepository vetRepository;
    private final RestTemplate restTemplate;
    private final EmailService emailService;

    @Value("${mercadopago.token}")
    private String mercadoPagoToken;

    public MercadoPagoService(VetRepository vetRepository, RestTemplate restTemplate, EmailService emailService) {
        this.vetRepository = vetRepository;
        this.restTemplate = restTemplate;
        this.emailService = emailService;
    }


    public void processPaymentStatus(String preApprovalId, Long vetId) {
        getStatus(preApprovalId, vetId);
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

    private String getPayerId(String preApprovalId){
        String apiUrl = "https://api.mercadopago.com/preapproval/" + preApprovalId;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + mercadoPagoToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<Map> response = restTemplate.exchange(apiUrl, HttpMethod.GET, entity, Map.class);

        Map<String, Object> payload = response.getBody();
        if (payload == null || !payload.containsKey("payer_id")) {
            throw new BadRequestException("No se encontró el campo 'payer_id' en la respuesta.");
        }

        return String.valueOf(payload.get("payer_id"));
    }

    private String getStatus(String preApprovalId, Long vetId) {

        String payerId = getPayerId(preApprovalId);

        String apiUrl = "https://api.mercadopago.com/v1/payments/search?payer.id=" + payerId;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + mercadoPagoToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<Map> response = restTemplate.exchange(apiUrl, HttpMethod.GET, entity, Map.class);

        Map<String, Object> payload = response.getBody();
        if (payload == null || !payload.containsKey("results")) {
            throw new BadRequestException("No se encontraron resultados en la búsqueda de pagos.");
        }

        List<Map<String, Object>> results = (List<Map<String, Object>>) payload.get("results");

        // filtro por ultima vfecha
        Optional<Map<String, Object>> latestPayment = results.stream()
                .max(Comparator.comparing(payment -> ZonedDateTime.parse((String) payment.get("date_created"))));

        //traigo data de la veterinaria
        VetRequest vetRequest = vetRepository.findVetById(vetId)
                .orElseThrow(() -> new NotFoundException("Vet not found with id: " + vetId));

        if (latestPayment.isPresent()) {
            Map<String, Object> payment = latestPayment.get();
            String status = (String) payment.get("status");

            if ("approved".equalsIgnoreCase(status)) {
                System.out.println("El pago fue aprobado, se actualizará el campo payment.");
                updateVetPaymentStatus(vetId, "paid");
                try {
                    emailService.sendPaymentConfirmationToVet(vetRequest.getName(), vetRequest.getEmail());
                    emailService.sendPaymentConfirmationToAdmin(vetRequest);
                } catch (MessagingException e) {
                    System.out.println("ERROR AL INTENTAR ENVIAR EL CORREO" + e);
                    throw new BadRequestException("Error al enviar el correo: " + e.getMessage(), e);
                } catch (IOException e) {
                    throw new RuntimeException("ERROR al enviar el email",e);
                }
            } else {
                System.out.println("El pago no está aprobado. Estado: " + status);
                throw new BadRequestException("El pago no está aprobado: " + status);
            }

            return status;
        } else {
            throw new BadRequestException("No se encontró un pago con una fecha válida.");
        }
    }

}
