package edu.vl4ds4m.tbank.exception;

public class InvalidCustomerIdException extends InvalidDataException {
    public InvalidCustomerIdException(int id) {
        super("Customer [id=" + id + "] isn't found");
    }
}
