package org.vl4ds4m.banking.converter.client;

import io.grpc.StatusRuntimeException;
import lombok.RequiredArgsConstructor;
import org.vl4ds4m.banking.common.entity.Currency;
import org.vl4ds4m.banking.common.entity.CurrencyRates;
import org.vl4ds4m.banking.common.entity.Money;
import org.vl4ds4m.banking.common.exception.ServiceException;
import org.vl4ds4m.banking.common.util.To;
import org.vl4ds4m.banking.rates.grpc.RatesGrpc;
import org.vl4ds4m.banking.rates.grpc.RatesRequest;
import org.vl4ds4m.banking.rates.grpc.RatesResponse;

import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class RatesClientImpl implements RatesClient {

    private final RatesGrpc.RatesBlockingStub stub;

    @Override
    public CurrencyRates getRates() {
        RatesResponse response;
        try {
            response = stub.getRates(RatesRequest.getDefaultInstance());
        } catch (StatusRuntimeException e) {
            throw new ServiceException("rates", e);
        }

        Currency base = To.currency(response.getBase());
        Map<Currency, Money> rates = response.getRatesList()
                .stream()
                .collect(Collectors.toMap(
                        e -> To.currency(e.getCurrency()),
                        e -> Money.of(e.getRate())));

        return new CurrencyRates(base, rates);
    }
}
