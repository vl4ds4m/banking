package org.vl4ds4m.banking.common.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties("services.keycloak")
@Validated
public record KeycloakProperties(
    @NotBlank
    String url,

    @NotBlank
    String realm,

    @NotBlank
    String clientId,

    @NotEmpty
    String clientSecret
) {}
