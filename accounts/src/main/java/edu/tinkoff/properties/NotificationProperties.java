package edu.tinkoff.properties;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties("services.notification")
@Validated
public record NotificationProperties(
        @NotBlank String url,
        @Positive int count
) {
}
