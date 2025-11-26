package org.vl4ds4m.banking.common.handler.circuitbreaker;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import lombok.RequiredArgsConstructor;
import org.slf4j.LoggerFactory;

@RequiredArgsConstructor
public class CircuitBreakerFactory {

    private final CircuitBreakerRegistry registry;

    public CircuitBreaker createCircuitBreaker(String serviceName) {
        var circuitBreaker = registry.circuitBreaker(serviceName);
        var log = LoggerFactory.getLogger(CircuitBreaker.class);

        circuitBreaker.getEventPublisher()
                .onStateTransition(event -> log.info(
                        "Circuit breaker [{}] state transition: {} -> {}",
                        event.getCircuitBreakerName(),
                        event.getStateTransition().getFromState(),
                        event.getStateTransition().getToState()));

        return circuitBreaker;
    }
}
