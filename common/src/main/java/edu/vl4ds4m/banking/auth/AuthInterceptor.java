package edu.vl4ds4m.banking.auth;

import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Profile(Auth.PROFILE)
public class AuthInterceptor implements ClientHttpRequestInterceptor {
    private final KeycloakAuthClient keycloakAuthClient;

    public AuthInterceptor(KeycloakAuthClient keycloakAuthClient) {
        this.keycloakAuthClient = keycloakAuthClient;
    }

    @Override
    public ClientHttpResponse intercept(
            HttpRequest request,
            byte[] body,
            ClientHttpRequestExecution execution
    ) throws IOException {
        String token = keycloakAuthClient.getToken();
        request.getHeaders().setBearerAuth(token);
        return execution.execute(request, body);
    }
}
