package edu.vl4ds4m.banking.converter.retry;

import org.springframework.retry.RetryContext;
import org.springframework.retry.backoff.BackOffContext;
import org.springframework.retry.backoff.BackOffInterruptedException;
import org.springframework.retry.backoff.BackOffPolicy;
import org.springframework.stereotype.Component;

@Component
public class ProgressiveBackOffPolicy implements BackOffPolicy {
    private static final long INITIAL_PERIOD = 50L;
    private static final long ADDITION = 50L;

    @Override
    public BackOffContext start(RetryContext context) {
        return new ProgressiveBackOffContext();
    }

    @Override
    public void backOff(BackOffContext backOffContext) throws BackOffInterruptedException {
        ProgressiveBackOffContext context = (ProgressiveBackOffContext) backOffContext;

        try {
            Thread.sleep(context.period);
        } catch (InterruptedException e) {
            throw new BackOffInterruptedException("Thread interrupted while sleeping", e);
        }

        context.period += ADDITION;
    }

    private static class ProgressiveBackOffContext implements BackOffContext {
        long period = INITIAL_PERIOD;
    }
}
