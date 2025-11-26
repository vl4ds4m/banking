package org.vl4ds4m.banking.common.handler.retry;

import lombok.RequiredArgsConstructor;
import org.springframework.retry.RetryListener;
import org.springframework.retry.RetryPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

@RequiredArgsConstructor
public class RetryTemplateFactory {

    private final ProgressiveRetryProperties properties;

    public RetryTemplate createRetryTemplate(String serviceName) {
        var retryTemplate = new RetryTemplate();
        retryTemplate.setRetryPolicy(createRetryPolicy());
        retryTemplate.setBackOffPolicy(createBackOffPolicy());
        retryTemplate.registerListener(createRetryListener(serviceName));
        return retryTemplate;
    }

    private RetryPolicy createRetryPolicy() {
        return new SimpleRetryPolicy(properties.attempts());
    }

    private ProgressiveBackOffPolicy createBackOffPolicy() {
        return new ProgressiveBackOffPolicy(properties.initial(), properties.addition());
    }

    private RetryListener createRetryListener(String serviceName) {
        return new RetryExecutionListener(serviceName);
    }
}
