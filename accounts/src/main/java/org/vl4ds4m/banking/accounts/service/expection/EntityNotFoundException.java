package org.vl4ds4m.banking.accounts.service.expection;

import org.vl4ds4m.banking.accounts.service.util.LogUtils;

public class EntityNotFoundException extends ServiceException {

    public static EntityNotFoundException with(Class<?> cls, Object... args) {
        var message = LogUtils.entityStr(cls, args) + " not found";
        return new EntityNotFoundException(message);
    }

    private EntityNotFoundException(String message) {
        super(message);
    }
}
