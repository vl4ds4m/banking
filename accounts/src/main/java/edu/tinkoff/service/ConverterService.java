package edu.tinkoff.service;

import edu.tinkoff.auth.KeycloakAuthClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class ConverterService {
    private final RestTemplate restTemplate;
    private final KeycloakAuthClient keycloakAuthClient;

    private String converterUrl;

    public ConverterService(RestTemplate restTemplate, KeycloakAuthClient keycloakAuthClient) {
        this.restTemplate = restTemplate;
        this.keycloakAuthClient = keycloakAuthClient;
    }

    @Value("${services.converter.url}")
    public void setConverterUrl(String converterUrl) {
        this.converterUrl = converterUrl;
    }

    public Map<String, Object> convert(String from, String to, double amount) {
        String token = keycloakAuthClient.getToken();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);
        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                converterUrl,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<>() {},
                from, to, amount
        );

        return response.getBody();
    }
}
