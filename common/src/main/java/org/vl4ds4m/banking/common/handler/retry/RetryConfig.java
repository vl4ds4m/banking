package org.vl4ds4m.banking.common.handler.retry;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.EnableRetry;

@Configuration
@EnableRetry
@EnableConfigurationProperties(ProgressiveRetryProperties.class)
@RequiredArgsConstructor
public class RetryConfig {

    private final ProgressiveRetryProperties progressiveRetryProps;

    @Bean
    public RetryTemplateFactory retryTemplateFactory() {
        return new RetryTemplateFactory(progressiveRetryProps);
    }
}
