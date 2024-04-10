package edu.tinkoff.config;

import edu.tinkoff.service.ConverterService;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig.SlidingWindowType;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class CircuitBreakerConfiguration {
    private static final Logger logger = LoggerFactory.getLogger(ConverterService.class);

    @Bean
    public CircuitBreaker circuitBreaker() {
        CircuitBreakerConfig config = CircuitBreakerConfig.custom()
                .failureRateThreshold(50f)
                .slidingWindowType(SlidingWindowType.TIME_BASED)
                .slidingWindowSize(60)
                .minimumNumberOfCalls(10)
                .waitDurationInOpenState(Duration.ofSeconds(10))
                .permittedNumberOfCallsInHalfOpenState(3)
                .build();

        CircuitBreakerRegistry registry = CircuitBreakerRegistry.of(config);
        CircuitBreaker circuitBreaker = registry.circuitBreaker("converter");
        circuitBreaker.getEventPublisher()
                .onSuccess(event -> logger.info(
                        "Success, state: {}",
                        circuitBreaker.getState()
                ))
                .onError(event -> logger.warn(
                        "Error, state: {}", circuitBreaker.getState()
                ))
                .onCallNotPermitted(event -> logger.warn(
                        "Call not permitted"
                ))
                .onReset(event -> logger.info(
                        "Reset"
                ))
                .onFailureRateExceeded(event -> logger.warn(
                        "Failure rate exceeded"
                ))
                .onIgnoredError(event -> logger.warn(
                        "Ignored error"
                ))
                .onSlowCallRateExceeded(event -> logger.warn(
                        "Slow call rate exceeded"
                ))
                .onStateTransition(event -> logger.info(
                        event.getStateTransition().toString()
                ));
        return circuitBreaker;
    }
}
