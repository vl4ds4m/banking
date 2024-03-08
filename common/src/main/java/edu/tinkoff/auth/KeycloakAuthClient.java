package edu.tinkoff.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
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
        if (clientId.isEmpty()) {
            return null;
        }

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
            return null;
        }
        return responseBody.get("access_token");
    }
}
