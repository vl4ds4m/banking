package org.vl4ds4m.banking.accounts.service.exception;

import org.vl4ds4m.banking.common.exception.InvalidQueryException;
import org.vl4ds4m.banking.common.util.To;

public class DuplicateEntityException extends InvalidQueryException {

    public DuplicateEntityException(Class<?> cls, Object... args) {
        super(To.string(cls, args) + " already exists");
    }
}
