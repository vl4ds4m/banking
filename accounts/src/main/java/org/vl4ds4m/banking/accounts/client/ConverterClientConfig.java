package org.vl4ds4m.banking.accounts.client;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.vl4ds4m.banking.common.handler.circuitbreaker.CircuitBreakerFactory;

import java.util.function.Supplier;

@Configuration
@RequiredArgsConstructor
public class ConverterClientConfig {

    public final CircuitBreakerFactory circuitBreakerFactory;

    @Bean
    public ConverterClient converterClient(ConverterClientImpl impl) {
        var circuitBreaker = circuitBreakerFactory.createCircuitBreaker("converter");
        return new ConverterDecorator(impl) {
            @Override
            protected <T> Supplier<T> decorateConvertCurrency(Supplier<T> fn) {
                return circuitBreaker.decorateSupplier(fn);
            }
        };
    }
}
