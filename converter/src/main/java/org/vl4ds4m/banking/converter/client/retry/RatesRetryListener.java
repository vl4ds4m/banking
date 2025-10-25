package org.vl4ds4m.banking.converter.client.retry;

import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.RetryListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class RatesRetryListener implements RetryListener {

    @Override
    public <T, E extends Throwable> void onError(
        RetryContext context,
        RetryCallback<T, E> callback,
        Throwable throwable
    ) {
        int retryCount = context.getRetryCount();
        log.warn("Fail to get rates, attempt #{}", retryCount);
    }
}
