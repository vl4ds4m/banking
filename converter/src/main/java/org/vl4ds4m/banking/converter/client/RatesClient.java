package org.vl4ds4m.banking.converter.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.vl4ds4m.banking.converter.client.rates.RatesApi;
import org.vl4ds4m.banking.converter.client.rates.model.RatesResponse;

@Slf4j
@RequiredArgsConstructor
public class RatesClient {

    private final RatesApi api;

    public RatesResponse getRates() {
        return api.getRates();
    }
}
