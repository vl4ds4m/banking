package edu.tinkoff.exception;

public class InvalidCustomerIdException extends InvalidDataException {
    public InvalidCustomerIdException(int id) {
        super("Customer [id=" + id + "] isn't found");
    }
}
