package edu.vl4ds4m.tbank.converter.exception;

public class InvalidCurrencyException extends RuntimeException {
    public final String currency;

    public InvalidCurrencyException(String currency) {
        this.currency = currency;
    }
}
