package org.vl4ds4m.banking.common.util;

import org.springframework.validation.Errors;
import org.vl4ds4m.banking.common.entity.Currency;
import org.vl4ds4m.banking.common.entity.Money;
import org.vl4ds4m.banking.common.exception.ServiceException;

import java.math.BigDecimal;

public class To {

    private To() {}

    public static String string(Class<?> cls, Object... args) {
        return EntityToString.string(cls, args);
    }

    public static String string(Errors errors) {
        return ErrorsToString.string(errors);
    }

    public static Money moneyOrReject(BigDecimal amount, String varName) throws ServiceException {
        return MoneyConverter.convertOrReject(amount, varName);
    }

    public static org.vl4ds4m.banking.common.grpc.Currency currency(Currency currency) {
        return GrpcCurrencyConverter.convert(currency);
    }

    public static Currency currency(org.vl4ds4m.banking.common.grpc.Currency currency) {
        return GrpcCurrencyConverter.convert(currency);
    }
}
