package org.vl4ds4m.banking.converter.service.exception;

public class InvalidCurrencyException extends RuntimeException {
    public final String currency;

    public InvalidCurrencyException(String currency) {
        super("Currency " + currency + " is invalid");
        this.currency = currency;
    }
}
