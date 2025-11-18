package org.vl4ds4m.banking.accounts.client.http;

import org.springframework.web.client.RestClientException;
import org.vl4ds4m.banking.accounts.client.ConverterClient;
import org.vl4ds4m.banking.common.entity.Currency;
import org.vl4ds4m.banking.common.entity.Money;
import org.vl4ds4m.banking.common.exception.ServiceException;
import org.vl4ds4m.banking.common.util.To;
import org.vl4ds4m.banking.converter.client.http.ConvertApi;
import org.vl4ds4m.banking.converter.client.http.invoker.ApiClient;
import org.vl4ds4m.banking.converter.client.http.model.ConvertCurrencyResponse;

public class ConverterHttpClient implements ConverterClient {

    private final ConvertApi api;

    public ConverterHttpClient(ApiClient client) {
        this.api = new ConvertApi(client);
    }

    @Override
    public Money convertCurrency(Currency source, Currency target, Money money) {
        var apiFrom = To.currency(source);
        var apiTo = To.currency(target);
        var amount = money.amount();

        ConvertCurrencyResponse response;
        try {
            response = api.convertCurrency(apiFrom, apiTo, amount);
        } catch (RestClientException e) {
            throw new ServiceException("converter", e.getMostSpecificCause());
        }

        var converted = response.getConvertedAmount();
        return Money.of(converted);
    }
}
