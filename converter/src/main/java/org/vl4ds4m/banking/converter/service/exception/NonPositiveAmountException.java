package org.vl4ds4m.banking.converter.service.exception;

import java.math.BigDecimal;

public class NonPositiveAmountException extends RuntimeException {
    public NonPositiveAmountException(BigDecimal amount) {
        super("Amount is non-positive: " + amount);
    }
}
