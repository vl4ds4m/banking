package org.vl4ds4m.banking.exception;

public class InvalidCustomerIdException extends InvalidDataException {
    public InvalidCustomerIdException(int id) {
        super("Customer[id=" + id + "] isn't found");
    }
}
