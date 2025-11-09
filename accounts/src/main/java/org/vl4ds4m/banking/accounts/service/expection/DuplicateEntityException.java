package org.vl4ds4m.banking.accounts.service.expection;

import org.vl4ds4m.banking.accounts.service.util.LogUtils;

public class DuplicateEntityException extends ServiceException {

    public static DuplicateEntityException with(Class<?> cls, Object... args) {
        var message = LogUtils.entityStr(cls, args) + " already exists";
        return new DuplicateEntityException(message);
    }

    private DuplicateEntityException(String message) {
        super(message);
    }
}
