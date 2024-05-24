package edu.tinkoff.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.Optional;

@Component
public class KeycloakAuthClient {
    private static final Logger log = LoggerFactory.getLogger(KeycloakAuthClient.class);

    private final RestTemplate restTemplate;

    private String url;
    private String clientId;
    private String clientSecret;

    public KeycloakAuthClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Value("${services.keycloak.url.get}")
    public void setUrl(String url) {
        this.url = url;
    }

    @Value("${services.keycloak.client.id}")
    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    @Value("${services.keycloak.client.secret}")
    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public Optional<String> getToken() {
        if (clientId.isEmpty()) {
            return Optional.empty();
        }

        log.info("Send a request to get {} access token", clientId);

        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("client_id", clientId);
        requestBody.add("client_secret", clientSecret);
        requestBody.add("grant_type", "client_credentials");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        ResponseEntity<Map<String, String>> responseEntity = restTemplate.exchange(
                url,
                HttpMethod.POST,
                new HttpEntity<>(requestBody, headers),
                new ParameterizedTypeReference<>() {}
        );

        Map<String, String> responseBody = responseEntity.getBody();
        if (responseBody == null) {
            return Optional.empty();
        }
        return Optional.of(responseBody.get("access_token"));
    }
}
