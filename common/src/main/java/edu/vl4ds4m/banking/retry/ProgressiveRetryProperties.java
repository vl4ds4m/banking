package edu.vl4ds4m.banking.retry;

import jakarta.validation.constraints.Min;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;

@ConfigurationProperties("retry.progressive")
@Validated
public record ProgressiveRetryProperties(
    @Min(1)
    @DefaultValue("4")
    byte attempts,

    @DefaultValue("50ms")
    Duration initial,

    @DefaultValue("50ms")
    Duration addition
) {}
