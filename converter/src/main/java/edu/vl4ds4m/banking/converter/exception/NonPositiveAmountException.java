package edu.vl4ds4m.banking.converter.exception;

import java.math.BigDecimal;

public class NonPositiveAmountException extends RuntimeException {
    public NonPositiveAmountException(BigDecimal amount) {
        super("Amount is non-positive: " + amount);
    }
}
