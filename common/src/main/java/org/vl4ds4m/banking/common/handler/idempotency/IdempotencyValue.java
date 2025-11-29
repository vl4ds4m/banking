package org.vl4ds4m.banking.common.handler.idempotency;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.http.HttpStatus;

public record IdempotencyValue(int status, String content) {

    public static final IdempotencyValue IN_PROGRESS = new IdempotencyValue(-1, "");

    @JsonIgnore
    public boolean isDone() {
        return status != -1;
    }

    @JsonIgnore
    public boolean isCacheable() {
        var code = HttpStatus.resolve(status);
        return code != null && !code.isError();
    }
}
