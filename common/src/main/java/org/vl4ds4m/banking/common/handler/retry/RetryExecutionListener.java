package org.vl4ds4m.banking.common.handler.retry;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.retry.RetryListener;
import org.springframework.core.retry.RetryPolicy;
import org.springframework.core.retry.Retryable;

@Slf4j
@RequiredArgsConstructor
public class RetryExecutionListener implements RetryListener {

    private final String serviceName;

    @Override
    public void beforeRetry(RetryPolicy retryPolicy, Retryable<?> retryable) {
        log.warn("Retry to send failed request to '{}' service", serviceName);
    }

}
