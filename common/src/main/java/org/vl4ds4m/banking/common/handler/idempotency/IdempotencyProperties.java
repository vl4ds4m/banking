package org.vl4ds4m.banking.common.handler.idempotency;

import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;

@ConfigurationProperties(IdempotencyProperties.PREFIX)
@Validated
public record IdempotencyProperties(
    @NotNull Duration ttl
) {
    static final String PREFIX = "idempotency";
}
