package org.vl4ds4m.banking.common.properties;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties("service.rates")
@Validated
public record RatesProperties(

        @NotBlank
        String host,

        @Min(0)
        @Max(65535)
        int port

) implements ServiceProperties {}
