package org.vl4ds4m.banking.common.properties;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

@ConfigurationProperties("service.converter")
@Valid
public record ConverterProperties(

    @DefaultValue("true")
    boolean grpc,

    @NotBlank
    String host,

    @Min(1)
    @Max(65535)
    int port
) {

    public String address() {
        return host + ":" + port;
    }
}
