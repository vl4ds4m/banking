package org.vl4ds4m.banking.common.auth;

import org.keycloak.representations.AccessTokenResponse;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.vl4ds4m.banking.common.exception.ServiceException;

import java.util.Objects;

public class KeycloakAuthClient {

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

    public String getToken() {
        String token = cachedToken;
        if (token == null) {
            token = postForToken();
            cachedToken = token;
        }
        return token;
    }

    public void invalidateToken() {
        cachedToken = null;
    }

    private String postForToken() {
        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("client_id", clientId);
        requestBody.add("client_secret", clientSecret);
        requestBody.add("grant_type", "client_credentials");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        try {
            var response = restTemplate.postForObject(
                    tokenUrl,
                    new HttpEntity<>(requestBody, headers),
                    AccessTokenResponse.class
            );
            Objects.requireNonNull(response, "AccessTokenResponse is null");
            return response.getToken();
        } catch (RestClientException | NullPointerException e) {
            throw new ServiceException("keycloak", e);
        }
    }
}
