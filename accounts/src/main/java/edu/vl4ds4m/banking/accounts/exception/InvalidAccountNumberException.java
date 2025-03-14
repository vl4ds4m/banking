package edu.vl4ds4m.banking.accounts.exception;

import edu.vl4ds4m.banking.exception.InvalidDataException;

public class InvalidAccountNumberException extends InvalidDataException {
    public InvalidAccountNumberException(int number) {
        super("Account [number=" + number + "] isn't found");
    }
}
