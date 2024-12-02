package org.vetti.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.vetti.utils.MPUtils;

import java.util.Map;

@RestController
@RequestMapping("/webhooks/mercadopago")
public class MPController {

    private MPUtils mpUtils;

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

}
