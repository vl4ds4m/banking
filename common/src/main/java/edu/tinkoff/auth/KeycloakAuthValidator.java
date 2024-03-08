package edu.tinkoff.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
public class KeycloakAuthValidator {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${services.keycloak.url.validate}")
    private String url;

    public boolean validateToken(String token) {
        ParameterizedTypeReference<Map<String, Object>> typeRef = new ParameterizedTypeReference<>() {};
        ResponseEntity<Map<String, Object>> responseEntity = restTemplate.exchange(
                url,
                HttpMethod.GET,
                HttpEntity.EMPTY,
                typeRef
        );

        Map<String, Object> responseBody = responseEntity.getBody();
        if (responseBody == null) {
            return false;
        }
        return responseBody.containsKey("access_token") && responseBody.get("access_token").equals(token);
    }
}
