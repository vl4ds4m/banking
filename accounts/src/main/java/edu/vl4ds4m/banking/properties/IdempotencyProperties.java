package edu.vl4ds4m.banking.properties;

import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;

@ConfigurationProperties("idempotency")
@Validated
public record IdempotencyProperties(
        @NotNull String[] paths,
        @NotNull Duration ttl
) {
}
