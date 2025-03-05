package edu.vl4ds4m.banking.exception;

public class InvalidCurrencyException extends RuntimeException {
    public final String currency;

    public InvalidCurrencyException(String currency) {
        this.currency = currency;
    }
}
