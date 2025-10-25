package org.vl4ds4m.banking.accounts.exception;

public class InvalidCustomerIdException extends InvalidDataException {
    public InvalidCustomerIdException(int id) {
        super("Customer[id=" + id + "] isn't found");
    }
}
