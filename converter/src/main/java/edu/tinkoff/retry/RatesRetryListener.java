package edu.tinkoff.retry;

import edu.tinkoff.service.RatesService;
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
    public <T, E extends Throwable> boolean open(RetryContext context, RetryCallback<T, E> callback) {
        logger.info("Try to get rates");
        return true;
    }

    @Override
    public <T, E extends Throwable> void onSuccess(
            RetryContext context,
            RetryCallback<T, E> callback,
            T result
    ) {
        logger.info("Succeed");
    }

    @Override
    public <T, E extends Throwable> void onError(
            RetryContext context,
            RetryCallback<T, E> callback,
            Throwable throwable
    ) {
        int retryCount = context.getRetryCount();
        logger.warn("Failed, attempts: {}", retryCount);
    }

    @Override
    public <T, E extends Throwable> void close(
            RetryContext context,
            RetryCallback<T, E> callback,
            Throwable throwable
    ) {
        logger.info("Completed");
    }
}
