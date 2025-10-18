package org.vl4ds4m.banking.messaging;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;


@ConfigurationProperties("messaging")
@Validated
public record MessagingProperties(
    @NotNull
    @Pattern(regexp = SINGLE_PATH)
    String endpoint,

    @NotNull
    @Pattern(regexp = SINGLE_PATH)
    String destinationPrefix
) {
    private static final String SINGLE_PATH = "^/[\\w-]+$";
}
