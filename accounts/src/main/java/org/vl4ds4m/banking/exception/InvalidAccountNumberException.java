package org.vl4ds4m.banking.exception;

public class InvalidAccountNumberException extends InvalidDataException {
    public InvalidAccountNumberException(int number) {
        super("Account[number=" + number + "] isn't found");
    }
}
