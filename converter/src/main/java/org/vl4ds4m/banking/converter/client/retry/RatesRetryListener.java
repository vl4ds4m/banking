package org.vl4ds4m.banking.converter.client.retry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.RetryListener;
import org.springframework.stereotype.Component;
import org.vl4ds4m.banking.converter.service.RatesService;

@Component
public class RatesRetryListener implements RetryListener {
    private static final Logger logger = LoggerFactory.getLogger(RatesService.class);

    @Override
    public <T, E extends Throwable> void onError(
        RetryContext context,
        RetryCallback<T, E> callback,
        Throwable throwable
    ) {
        int retryCount = context.getRetryCount();
        logger.warn("Fail to get rates, attempt #{}", retryCount);
    }
}
