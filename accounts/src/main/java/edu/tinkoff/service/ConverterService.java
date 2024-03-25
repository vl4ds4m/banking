package edu.tinkoff.service;

import edu.tinkoff.auth.KeycloakAuthClient;
import edu.tinkoff.dto.CurrencyMessage;
import edu.tinkoff.model.Currency;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Objects;

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

    public BigDecimal convert(Currency from, Currency to, BigDecimal amount) {
        String token = keycloakAuthClient.getToken();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        ResponseEntity<CurrencyMessage> response = restTemplate.exchange(
                converterUrl,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                CurrencyMessage.class,
                from, to, amount
        );

        return Objects.requireNonNull(response.getBody()).amount();
    }
}
