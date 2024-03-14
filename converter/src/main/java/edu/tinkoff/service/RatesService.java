package edu.tinkoff.service;

import edu.tinkoff.auth.KeycloakAuthClient;
import edu.tinkoff.model.RatesResposne;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class RatesService {

    @Value("${services.currency-rates.url}")
    private String currencyRatesUrl;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private KeycloakAuthClient keycloakAuthClient;

    public RatesResposne getRatesResponse() {
        String token = keycloakAuthClient.getToken();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);
        ResponseEntity<RatesResposne> response = restTemplate.exchange(
                currencyRatesUrl,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                RatesResposne.class
        );

        return response.getBody();
    }
}
