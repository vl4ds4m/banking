package org.vl4ds4m.banking.accounts.client;

import lombok.RequiredArgsConstructor;
import org.vl4ds4m.banking.common.entity.Currency;
import org.vl4ds4m.banking.common.entity.Money;

import java.util.function.Supplier;

@RequiredArgsConstructor
public abstract class ConverterDecorator implements ConverterClient {

    private final ConverterClient client;

    protected <T> Supplier<T> decorateConvertCurrency(Supplier<T> fn) {
        return fn;
    }

    @Override
    public Money convertCurrency(Currency source, Currency target, Money money) {
        Supplier<Money> fn = () -> client.convertCurrency(source, target, money);
        fn = decorateConvertCurrency(fn);
        return fn.get();
    }
}
