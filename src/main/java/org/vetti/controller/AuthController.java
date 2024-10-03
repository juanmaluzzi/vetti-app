package org.vetti.controller;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@RestController
public class AuthController {

    // van a quedar hardcodeados por error con el .env
    private String validApiKey = "2c98a622-b0e2-4b24-8deb-e3a3f4b54b7e";

    private String clientId = "Dgd31Xpn0ppSkXsDV3hWLGWlBoS96Mnr";
    private String clientSecret = "cpKh3VKN_-trH6OCOUgWMYkQHBu9_C5ywqByFefrdCZjMGFfK8IdxB9gMW63tYS7";
    private String audience = "https://dev-k1n7shfb1jvuxkvz.us.auth0.com/api/v2/";
    private String grantType = "client_credentials";

    @PostMapping("/getToken")
    public ResponseEntity<Map<String, String>> getAuthToken(@RequestHeader("Authorization") String apiKey) {
        if (!apiKey.equals(validApiKey)) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Unauthorized: Invalid API Key");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }

        RestTemplate restTemplate = new RestTemplate();

        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("client_id", clientId);
        requestBody.put("client_secret", clientSecret);
        requestBody.put("audience", audience);
        requestBody.put("grant_type", grantType);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange("https://dev-k1n7shfb1jvuxkvz.us.auth0.com/oauth/token", HttpMethod.POST, requestEntity, Map.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                Map<String, Object> responseBody = response.getBody();
                String accessToken = (String) responseBody.get("access_token");

                Map<String, String> customResponse = new HashMap<>();
                customResponse.put("access_token", "Bearer " + accessToken);

                return ResponseEntity.ok(customResponse);
            } else {
                return ResponseEntity.status(response.getStatusCode()).body(null);
            }
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Error retrieving access token: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}
