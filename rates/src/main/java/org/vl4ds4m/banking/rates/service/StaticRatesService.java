package org.vl4ds4m.banking.rates.service;

import lombok.extern.slf4j.Slf4j;
import org.vl4ds4m.banking.common.entity.CurrencyRates;

@Slf4j
public class StaticRatesService implements RatesService {

    private final CurrencyRates currencyRates;

    public StaticRatesService(CurrencyRates currencyRates) {
        log.info("Create StaticRatesService with currencyRates = {}", currencyRates);
        this.currencyRates = currencyRates;
    }

    public CurrencyRates getRates() {
        return currencyRates;
    }

}
