package org.vl4ds4m.banking.accounts.service.expection;

import org.vl4ds4m.banking.common.exception.InvalidQueryException;
import org.vl4ds4m.banking.common.util.To;

public class EntityNotFoundException extends InvalidQueryException {

    public EntityNotFoundException(Class<?> cls, Object... args) {
        super(To.string(cls, args) + " not found");
    }
}
