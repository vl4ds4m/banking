package org.vl4ds4m.banking.common.handler.retry;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.RetryListener;

@Slf4j
@RequiredArgsConstructor
public class RetryExecutionListener implements RetryListener {

    private final String serviceName;

    @Override
    public <T, E extends Throwable> void onError(
        RetryContext context,
        RetryCallback<T, E> callback,
        Throwable throwable
    ) {
        int retryCount = context.getRetryCount();
        log.warn("Fail to send request [{}], attempt #{}", serviceName, retryCount);
    }
}
