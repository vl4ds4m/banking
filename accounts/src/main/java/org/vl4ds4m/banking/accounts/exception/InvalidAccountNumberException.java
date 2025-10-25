package org.vl4ds4m.banking.accounts.exception;

public class InvalidAccountNumberException extends InvalidDataException {
    public InvalidAccountNumberException(int number) {
        super("Account[number=" + number + "] isn't found");
    }
}
