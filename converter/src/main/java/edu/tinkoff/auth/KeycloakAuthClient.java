package edu.tinkoff.auth;

import com.auth0.jwt.JWT;
import org.keycloak.representations.AccessTokenResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.Optional;

@Component
public class KeycloakAuthClient {
    private static final Logger log = LoggerFactory.getLogger(KeycloakAuthClient.class);

    private final RestTemplate restTemplate;

    private String tokenUrl;
    private String clientId;
    private String clientSecret;

    private String cachedToken;

    public KeycloakAuthClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Value("${services.keycloak.url}")
    public void setTokenUrl(String url) {
        tokenUrl = url + "/token";
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

        if (!isTokenExpired()) {
            cachedToken = postForToken();
        }

        return Optional.of(cachedToken);
    }

    private boolean isTokenExpired() {
        if (cachedToken == null) {
            return false;
        }

        return !JWT.decode(cachedToken).getExpiresAt().after(new Date());
    }

    private String postForToken() {
        log.info("Send a request to get {} access token", clientId);

        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("client_id", clientId);
        requestBody.add("client_secret", clientSecret);
        requestBody.add("grant_type", "client_credentials");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        var response = restTemplate.postForObject(
                tokenUrl,
                new HttpEntity<>(requestBody, headers),
                AccessTokenResponse.class
        );

        if (response == null) {
            throw new RuntimeException("Access token response is null");
        }

        return response.getToken();
    }
}
