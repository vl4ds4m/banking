package org.vl4ds4m.banking.notification;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.convert.DurationUnit;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

@ConfigurationProperties(NotificationProperties.PREFIX)
@Validated
public record NotificationProperties(
    @NotBlank
    String host,

    @NotBlank
    String url,

    @NotNull
    @Positive
    Integer count,

    @DurationUnit(ChronoUnit.SECONDS)
    @NotNull
    Duration delay
) {
    static final String PREFIX = "notification";
}
