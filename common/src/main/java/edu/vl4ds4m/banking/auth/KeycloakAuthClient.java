package edu.vl4ds4m.banking.auth;

import com.auth0.jwt.JWT;
import org.keycloak.representations.AccessTokenResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Profile;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.Optional;

@Component
@Profile(Auth.PROFILE)
@EnableConfigurationProperties(KeycloakProperties.class)
public class KeycloakAuthClient {
    private static final Logger logger = LoggerFactory.getLogger(KeycloakAuthClient.class);

    private final RestTemplate restTemplate;

    private final String tokenUrl;
    private final String clientId;
    private final String clientSecret;

    private String cachedToken;

    public KeycloakAuthClient(RestTemplate restTemplate, KeycloakProperties properties) {
        this.restTemplate = restTemplate;
        this.tokenUrl = String.format(
            "%s/realms/%s/protocol/openid-connect/token",
            properties.url(), properties.realm());
        this.clientId = properties.clientId();
        this.clientSecret = properties.clientSecret();
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
        logger.debug("Send a request to get {} access token", clientId);

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

        return response.getToken();
    }
}
