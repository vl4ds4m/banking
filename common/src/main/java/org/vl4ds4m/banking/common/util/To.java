package org.vl4ds4m.banking.common.util;

import org.springframework.validation.Errors;

public class To {

    private To() {}

    public static String string(Class<?> cls, Object... args) {
        return EntityToString.string(cls, args);
    }

    public static String string(Errors errors) {
        return ErrorsToString.string(errors);
    }
}
