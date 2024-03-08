package edu.tinkoff.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
public class KeycloakAuthClient {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${services.keycloak.url.get}")
    private String url;

    @Value("${services.keycloak.client.id}")
    private String clientId;

    @Value("${services.keycloak.client.secret}")
    private String clientSecret;

    public String getToken() {
        Map<String, String> requestBody = Map.of(
                "client_id", clientId,
                "client_secret", clientSecret,
                "grant_type", "client_credentials"
        );
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(requestBody, headers);
        ParameterizedTypeReference<Map<String, Object>> typeRef = new ParameterizedTypeReference<>() {};

        ResponseEntity<Map<String, Object>> responseEntity = restTemplate.exchange(
                url,
                HttpMethod.POST,
                requestEntity,
                typeRef
        );

        Map<String, Object> responseBody = responseEntity.getBody();
        if (responseBody == null) {
            return null;
        }
        return responseBody.get("access_token").toString();
    }
}
