package org.vl4ds4m.banking.common.handler.idempotency;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.vl4ds4m.banking.common.exception.InvalidQueryException;

import java.time.Duration;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
public class RedisIdempotencyHandler implements IdempotencyHandler {

    private final RedisTemplate<String, IdempotencyValue> redisTemplate;

    private final Duration ttl;

    @Override
    public boolean isIdempotencyKeyValid(@Nullable String key) {
        if (key == null) {
            return false;
        }
        try {
            UUID.fromString(key);
        } catch (IllegalArgumentException e) {
            return false;
        }
        return true;
    }

    @Override
    public @Nullable IdempotencyValue preHandle(String idempotencyKey) {
        var valueOps = redisTemplate.boundValueOps(idempotencyKey);
        IdempotencyValue result = getOrSet(valueOps);
        if (result == null) {
            return null;
        }
        if (!result.isDone()) {
            throw new InvalidQueryException("Request in progress. Try to get result later.");
        }
        log.debug("Get cached content for key = {}", idempotencyKey);
        return result;
    }

    @Override
    public void postHandle(String idempotencyKey, IdempotencyValue idempotencyValue) {
        var valueOps = redisTemplate.boundValueOps(idempotencyKey);
        if (idempotencyValue.isCacheable()) {
            valueOps.set(idempotencyValue, ttl);
            log.debug("Cache content for key = {}", idempotencyKey);
        } else {
            valueOps.getAndDelete();
        }
    }

    private @Nullable IdempotencyValue getOrSet(BoundValueOperations<?, IdempotencyValue> valueOps) {
        IdempotencyValue value = valueOps.get();
        Boolean absent = valueOps.setIfAbsent(IdempotencyValue.IN_PROGRESS, ttl);

        // must true or false
        if (absent == null) throw new IllegalStateException();
        if (absent) return null;

        // must be non-null after absent check
        // theoretical bug: get operation may return null, but before setIfAbsent another app may set non-null
        // and exception occurs
        if (value == null) throw new IllegalStateException();
        return value;
    }
}
