package org.vl4ds4m.banking.common.handler.circuitbreaker;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig.SlidingWindowType;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class CircuitBreakerConfig {
    @Bean
    public CircuitBreakerFactory circuitBreakerFactory() {
        var config = io.github.resilience4j.circuitbreaker.CircuitBreakerConfig.custom()
            .failureRateThreshold(50f)
            .slidingWindowType(SlidingWindowType.TIME_BASED)
            .slidingWindowSize(60)
            .minimumNumberOfCalls(5)
            .waitDurationInOpenState(Duration.ofSeconds(10))
            .permittedNumberOfCallsInHalfOpenState(3)
            .build();

        var registry = CircuitBreakerRegistry.of(config);
        return new CircuitBreakerFactory(registry);
    }
}
