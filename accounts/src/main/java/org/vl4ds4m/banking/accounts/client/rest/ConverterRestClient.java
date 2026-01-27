package org.vl4ds4m.banking.accounts.client.rest;

import org.springframework.web.client.RestClientException;
import org.vl4ds4m.banking.accounts.client.ConverterClientImpl;
import org.vl4ds4m.banking.common.entity.Currency;
import org.vl4ds4m.banking.common.entity.Money;
import org.vl4ds4m.banking.common.exception.ServiceException;
import org.vl4ds4m.banking.common.util.To;
import org.vl4ds4m.banking.converter.openapi.client.api.ConvertApi;
import org.vl4ds4m.banking.converter.openapi.client.invoke.ApiClient;
import org.vl4ds4m.banking.converter.openapi.client.model.ConvertCurrencyResponse;

public class ConverterRestClient implements ConverterClientImpl {

    private final ConvertApi api;

    public ConverterRestClient(ApiClient client) {
        this.api = new ConvertApi(client);
    }

    @Override
    public Money convertCurrency(Currency source, Currency target, Money money) {
        var apiFrom = To.restCurrency(source);
        var apiTo = To.restCurrency(target);
        var amount = money.amount();

        ConvertCurrencyResponse response;
        try {
            response = api.convertCurrency(apiFrom, apiTo, amount);
        } catch (RestClientException e) {
            throw new ServiceException("converter", e);
        }

        var converted = response.getConvertedAmount();
        return Money.of(converted);
    }
}
