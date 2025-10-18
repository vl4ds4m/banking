package org.vl4ds4m.banking.rates.retry;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

@Configuration
@EnableRetry
@EnableConfigurationProperties(ProgressiveRetryProperties.class)
public class RetryConfig {
    @Bean
    public RetryTemplate retryTemplate(
        ProgressiveRetryProperties properties,
        RatesRetryListener listener
    ) {
        RetryTemplate retryTemplate = new RetryTemplate();
        retryTemplate.setBackOffPolicy(
            new ProgressiveBackOffPolicy(properties.initial(), properties.addition()));
        retryTemplate.registerListener(listener);
        retryTemplate.setRetryPolicy(new SimpleRetryPolicy(properties.attempts()));
        return retryTemplate;
    }
}
