package org.vl4ds4m.banking.common.util;

import org.vl4ds4m.banking.common.entity.Money;
import org.vl4ds4m.banking.common.exception.ServiceException;

import java.math.BigDecimal;

class MoneyConverter {

    private MoneyConverter() {}

    static Money convertOrReject(BigDecimal amount, String varName) {
        if (Money.isValid(amount)) return Money.of(amount);
        throw new ServiceException(varName + " must be zero or positive");
    }
}
