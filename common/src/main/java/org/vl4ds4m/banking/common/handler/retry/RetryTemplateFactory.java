package org.vl4ds4m.banking.common.handler.retry;

import lombok.RequiredArgsConstructor;
import org.springframework.core.retry.RetryListener;
import org.springframework.core.retry.RetryPolicy;
import org.springframework.core.retry.RetryTemplate;

@RequiredArgsConstructor
public class RetryTemplateFactory {

    private final ProgressiveRetryProperties props;

    public RetryTemplate createRetryTemplate(String serviceName) {
        var retryTemplate = new RetryTemplate();
        retryTemplate.setRetryPolicy(createRetryPolicy());
        retryTemplate.setRetryListener(createRetryListener(serviceName));
        return retryTemplate;
    }

    private RetryPolicy createRetryPolicy() {
        var backOff = new ProgressiveBackOff(props.attempts(), props.initial(), props.addition());
        return RetryPolicy.builder()
                .backOff(backOff)
                .build();
    }

    private RetryListener createRetryListener(String serviceName) {
        return new RetryExecutionListener(serviceName);
    }

}
