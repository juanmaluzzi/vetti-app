package org.vetti.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class AuthService {


    @Value("${auth.validApiKey}")
    private String validApiKey;
    @Value("${auth.clientId}")
    private String clientId;
    @Value("${auth.clientSecret}")
    private String clientSecret;
    @Value("${auth.audience}")
    private String audience;
    private String grantType = "client_credentials";

    @Value("${auth.authUrl}")
    private String authUrl;

    public boolean isValidApiKey(String apiKey) {
        return validApiKey.equals(apiKey);
    }

    public Map<String, String> fetchAuthToken() throws Exception {
        RestTemplate restTemplate = new RestTemplate();

        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("client_id", clientId);
        requestBody.put("client_secret", clientSecret);
        requestBody.put("audience", audience);
        requestBody.put("grant_type", grantType);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<Map> response = restTemplate.exchange(authUrl, HttpMethod.POST, requestEntity, Map.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            Map<String, Object> responseBody = response.getBody();
            String accessToken = (String) responseBody.get("access_token");

            Map<String, String> customResponse = new HashMap<>();
            customResponse.put("access_token", "Bearer " + accessToken);

            log.info("Token obtenido: {}", customResponse);
            return customResponse;
        } else {
            log.error("Error en la respuesta de Auth0: {}", response.getStatusCode());
            throw new RuntimeException("Error fetching access token");
        }
    }
}
