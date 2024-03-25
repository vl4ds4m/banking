package edu.tinkoff.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthValidator implements HandlerInterceptor {
    private final KeycloakAuthValidator keycloakAuthValidator;

    public AuthValidator(KeycloakAuthValidator keycloakAuthValidator) {
        this.keycloakAuthValidator = keycloakAuthValidator;
    }

    @Override
    public boolean preHandle(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull Object handler
    ) {
        String token = request.getHeader(HttpHeaders.AUTHORIZATION);
        return keycloakAuthValidator.validateTokens(token);
    }
}
