package edu.vl4ds4m.banking.accounts.exception;

import edu.vl4ds4m.banking.exception.InvalidDataException;

public class InvalidCustomerIdException extends InvalidDataException {
    public InvalidCustomerIdException(int id) {
        super("Customer [id=" + id + "] isn't found");
    }
}
