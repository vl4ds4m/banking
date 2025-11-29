package org.vl4ds4m.banking.common.handler.idempotency;

import org.jspecify.annotations.Nullable;
import org.springframework.util.StringUtils;

public interface IdempotencyHandler {

    default boolean isIdempotencyKeyValid(@Nullable String key) {
        return StringUtils.hasText(key);
    }

    default @Nullable IdempotencyValue preHandle(String idempotencyKey) {
        return null;
    }

    default void postHandle(String idempotencyKey, IdempotencyValue idempotencyValue) {}
}
