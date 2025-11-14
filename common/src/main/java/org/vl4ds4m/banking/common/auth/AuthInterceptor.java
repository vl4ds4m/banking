package org.vl4ds4m.banking.common.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;

@RequiredArgsConstructor
public class AuthInterceptor implements ClientHttpRequestInterceptor {

    private final KeycloakAuthClient keycloakAuthClient;

    @Override
    public ClientHttpResponse intercept(
            HttpRequest request,
            byte[] body,
            ClientHttpRequestExecution execution
    ) throws IOException {
        String token = keycloakAuthClient.getToken();
        request.getHeaders().setBearerAuth(token);
        ClientHttpResponse response = execution.execute(request, body);
        HttpStatusCode statusCode = response.getStatusCode();
        if (statusCode.isSameCodeAs(HttpStatus.UNAUTHORIZED)) {
            keycloakAuthClient.invalidateToken();
        }
        return response;
    }
}
