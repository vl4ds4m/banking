package org.vl4ds4m.banking.converter.http.client.retry;

import org.springframework.retry.RetryContext;
import org.springframework.retry.backoff.BackOffContext;
import org.springframework.retry.backoff.BackOffInterruptedException;
import org.springframework.retry.backoff.BackOffPolicy;

import java.time.Duration;

public class ProgressiveBackOffPolicy implements BackOffPolicy {
    private final Duration initial;
    private final Duration addition;

    public ProgressiveBackOffPolicy(Duration initial, Duration addition) {
        this.initial = initial;
        this.addition = addition;
    }

    @Override
    public BackOffContext start(RetryContext context) {
        return new ProgressiveBackOffContext(initial);
    }

    @Override
    public void backOff(BackOffContext backOffContext) throws BackOffInterruptedException {
        ProgressiveBackOffContext context = (ProgressiveBackOffContext) backOffContext;
        try {
            Thread.sleep(context.period);
        } catch (InterruptedException e) {
            throw new BackOffInterruptedException("Thread interrupted while sleeping", e);
        }
        context.period = context.period.plus(addition);
    }

    private static class ProgressiveBackOffContext implements BackOffContext {
        Duration period;

        ProgressiveBackOffContext(Duration initial) {
            this.period = initial;
        }
    }
}
