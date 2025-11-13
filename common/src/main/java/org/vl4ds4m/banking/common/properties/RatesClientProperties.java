package org.vl4ds4m.banking.common.properties;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import static org.vl4ds4m.banking.common.properties.RatesClientProperties.PREFIX;

@ConfigurationProperties(PREFIX)
@Validated
public record RatesClientProperties(

    @NotBlank
    String host,

    @NotNull
    @Min(0)
    @Max(65535)
    Integer port

) implements ServiceProperties {

    public static final String PREFIX = "client.rates";
}
