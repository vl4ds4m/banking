package edu.tinkoff.auth;

import org.keycloak.jose.jwk.JWK;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;
import java.util.List;
import java.util.Map;

@Component
public class KeycloakAuthValidator {
    private final RestTemplate restTemplate;

    private String url;
    private String clientId;

    public KeycloakAuthValidator(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Value("${services.keycloak.url.validate}")
    public void setUrl(String url) {
        this.url = url;
    }

    @Value("${services.keycloak.client.id}")
    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public boolean validateTokens(String token) {
        if (clientId.isEmpty()) {
            return true;
        }

        List<JWK> jwkList = restTemplate.exchange(
                url,
                HttpMethod.GET,
                HttpEntity.EMPTY,
                new ParameterizedTypeReference<Map<String, List <JWK>>>() {}
        ).getBody().get("keys");

        String jwtHeader = new String(Base64.getDecoder().decode(
                token.split(" ")[1].split("\\.")[0]
        ));

        for (JWK jwk : jwkList) {
            if (jwtHeader.contains(jwk.getKeyId())) {
                return true;
            }
        }

        return false;
    }
}
