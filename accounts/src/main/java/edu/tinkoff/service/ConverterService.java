package edu.tinkoff.service;

import edu.tinkoff.auth.KeycloakAuthClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class ConverterService {

    @Value("${services.converter.url}")
    private String converterUrl;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private KeycloakAuthClient keycloakAuthClient;

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
