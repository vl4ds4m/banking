package edu.vl4ds4m.banking.rates.retry;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

@Configuration
@EnableRetry
public class RetryConfig {
    @Bean
    public RetryTemplate retryTemplate(
            ProgressiveBackOffPolicy backOffPolicy,
            RatesRetryListener retryListener
    ) {
        RetryTemplate retryTemplate = new RetryTemplate();
        retryTemplate.setBackOffPolicy(backOffPolicy);
        retryTemplate.registerListener(retryListener);
        retryTemplate.setRetryPolicy(new SimpleRetryPolicy(4));
        return retryTemplate;
    }
}
