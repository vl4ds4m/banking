package org.vl4ds4m.banking.accounts.service.expection;

import org.vl4ds4m.banking.common.exception.ServiceException;
import org.vl4ds4m.banking.common.util.To;

public class EntityNotFoundException extends ServiceException {

    public EntityNotFoundException(Class<?> cls, Object... args) {
        super(To.string(cls, args) + " not found");
    }
}
