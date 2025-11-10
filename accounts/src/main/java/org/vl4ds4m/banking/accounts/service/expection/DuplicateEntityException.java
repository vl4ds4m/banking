package org.vl4ds4m.banking.accounts.service.expection;

import org.vl4ds4m.banking.common.util.To;

public class DuplicateEntityException extends ServiceException {

    public DuplicateEntityException(Class<?> cls, Object... args) {
        super(To.string(cls, args) + " already exists");
    }
}
