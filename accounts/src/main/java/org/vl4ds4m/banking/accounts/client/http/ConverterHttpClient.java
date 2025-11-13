package org.vl4ds4m.banking.accounts.client.http;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.client.RestClientException;
import org.vl4ds4m.banking.accounts.client.ConverterClient;
import org.vl4ds4m.banking.common.entity.Currency;
import org.vl4ds4m.banking.common.entity.Money;
import org.vl4ds4m.banking.common.exception.ServiceException;
import org.vl4ds4m.banking.converter.client.http.ConvertApi;
import org.vl4ds4m.banking.converter.client.http.invoker.ApiClient;
import org.vl4ds4m.banking.converter.client.http.model.ConvertCurrencyResponse;

@Slf4j
public class ConverterHttpClient implements ConverterClient {

    private final ConvertApi api;

    public ConverterHttpClient(ApiClient client) {
        this.api = new ConvertApi(client);
    }

    @Override
    public Money convertCurrency(Currency source, Currency target, Money money) {
        var apiFrom = toApiCurrency(source);
        var apiTo = toApiCurrency(target);
        var amount = money.amount();

        log.info("Request currency conversion: {}, {} -> {}", amount, source, target);

        ConvertCurrencyResponse response;
        try {
            response = api.convertCurrency(apiFrom, apiTo, amount);
        } catch (RestClientException e) {
            throw new ServiceException("converter", e.getMostSpecificCause());
        }

        var converted = response.getConvertedAmount();
        return Money.of(converted);
    }

    private static org.vl4ds4m.banking.converter.client.http.model.Currency toApiCurrency(Currency currency) {
        return org.vl4ds4m.banking.converter.client.http.model.Currency.fromValue(currency.name());
    }
}
