package org.vl4ds4m.banking.rates.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileUrlResource;
import org.vl4ds4m.banking.common.entity.Currency;
import org.vl4ds4m.banking.common.entity.CurrencyRates;
import org.vl4ds4m.banking.common.entity.Money;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.util.Map;
import java.util.stream.Collectors;

@Configuration
public class RatesServiceConfig {

    @Bean
    public RatesService ratesService(@Value("${rates.filename}") String location) throws IOException {
        var resource = new FileUrlResource(location);
        Map<String, Object> config = new Yaml().load(resource.getInputStream());
        CurrencyRates currencyRates = createCurrencyRates(config);
        return new StaticRatesService(currencyRates);
    }

    private CurrencyRates createCurrencyRates(Map<String, Object> config) {
        var base = Currency.valueOf("" + config.get("base"));
        var rates = ((Map<?, ?>) config.get("rates"))
                .entrySet()
                .stream()
                .collect(Collectors.toMap(
                        e -> Currency.valueOf("" + e.getKey()),
                        e -> Money.of(e.getValue())));
        return new CurrencyRates(base, rates);
    }

}
