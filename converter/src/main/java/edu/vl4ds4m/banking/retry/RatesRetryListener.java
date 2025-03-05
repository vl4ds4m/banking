package edu.vl4ds4m.banking.retry;

import edu.vl4ds4m.banking.service.RatesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.RetryListener;
import org.springframework.stereotype.Component;

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
