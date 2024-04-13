package edu.tinkoff.exception;

public class InvalidAccountNumberException extends InvalidDataException {
    public InvalidAccountNumberException(int number) {
        super("Account [number=" + number + "] isn't found");
    }
}
