package edu.vl4ds4m.banking.retry;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.RetryListener;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

import java.util.List;

@Configuration
@EnableRetry
@EnableConfigurationProperties(ProgressiveRetryProperties.class)
public class RetryConfig {
    @Bean
    public RetryTemplate retryTemplate(
        ProgressiveRetryProperties properties,
        List<RetryListener> retryListeners
    ) {
        RetryTemplate retryTemplate = new RetryTemplate();
        retryTemplate.setBackOffPolicy(
            new ProgressiveBackOffPolicy(properties.initial(), properties.addition()));
        retryListeners.forEach(retryTemplate::registerListener);
        retryTemplate.setRetryPolicy(new SimpleRetryPolicy(properties.attempts()));
        return retryTemplate;
    }
}
