package org.vl4ds4m.banking.common.handler.retry;

import lombok.RequiredArgsConstructor;
import org.springframework.util.backoff.BackOff;
import org.springframework.util.backoff.BackOffExecution;

import java.time.Duration;

@RequiredArgsConstructor
public class ProgressiveBackOff implements BackOff {

    private final int maxAttempts;

    private final Duration initial;

    private final Duration addition;

    @Override
    public BackOffExecution start() {
        return new ProgressiveBackOffExecution();
    }

    private class ProgressiveBackOffExecution implements BackOffExecution {

        Duration time = initial;

        int attempt = 1;

        @Override
        public long nextBackOff() {
            if (attempt >= maxAttempts) {
                return STOP;
            }
            if (attempt == 1) {
                time = initial;
            } else {
                time = time.plus(addition);
            }
            attempt++;
            return time.toMillis();
        }

    }

}
