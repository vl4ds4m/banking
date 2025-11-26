package org.vl4ds4m.banking.converter.client;

import lombok.RequiredArgsConstructor;
import org.vl4ds4m.banking.converter.entity.CurrencyRates;

import java.util.function.Supplier;

@RequiredArgsConstructor
public abstract class RatesDecorator implements RatesClient {

    private final RatesClient client;

    protected <T> Supplier<T> decorateGetRates(Supplier<T> fn) {
        return fn;
    }

    @Override
    public CurrencyRates getRates() {
        Supplier<CurrencyRates> fn = client::getRates;
        fn = decorateGetRates(fn);
        return fn.get();
    }
}
