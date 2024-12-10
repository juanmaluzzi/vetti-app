package org.vetti.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.vetti.model.dto.CheckPaymentStatusDTO;
import org.vetti.service.MercadoPagoService;

@RestController
@RequestMapping("/mercadopago")
public class MPController {

    @Autowired
    private final MercadoPagoService mercadoPagoService;

    public MPController(MercadoPagoService mercadoPagoService) {
        this.mercadoPagoService = mercadoPagoService;
    }

    @PostMapping
    public ResponseEntity<String> handleWebhook(@RequestBody String payload) {
        try {
            System.out.println("Payload recibido: " + payload);
            return ResponseEntity.ok("Webhook recibido correctamente");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing webhook");
        }
    }

    @PostMapping("/processPaymentStatus")
    public ResponseEntity<Void> checkPaymentStatus(@RequestBody CheckPaymentStatusDTO checkPaymentStatusDTO) {
        mercadoPagoService.processPaymentStatus(checkPaymentStatusDTO.getPreApprovalId(), checkPaymentStatusDTO.getVetId());
        return ResponseEntity.ok().build();
    }
}
