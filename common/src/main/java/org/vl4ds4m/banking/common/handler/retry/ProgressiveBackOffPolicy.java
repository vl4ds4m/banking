package org.vl4ds4m.banking.common.handler.retry;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.retry.RetryContext;
import org.springframework.retry.backoff.BackOffContext;
import org.springframework.retry.backoff.BackOffInterruptedException;
import org.springframework.retry.backoff.BackOffPolicy;

import java.time.Duration;

@RequiredArgsConstructor
public class ProgressiveBackOffPolicy implements BackOffPolicy {

    private final Duration initial;

    private final Duration addition;

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

    @AllArgsConstructor
    private static class ProgressiveBackOffContext implements BackOffContext {
        Duration period;
    }
}
