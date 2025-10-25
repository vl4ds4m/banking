package org.vl4ds4m.banking.converter.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.vl4ds4m.banking.converter.api.model.Currency;
import org.vl4ds4m.banking.converter.client.rates.model.RatesResponse;
import org.vl4ds4m.banking.converter.service.exception.NonPositiveAmountException;
import org.vl4ds4m.banking.converter.service.exception.RatesServiceException;

import java.math.BigDecimal;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ConverterServiceTest {
    private final RatesService rates = Mockito.mock(RatesService.class);

    private final ConverterService service = new ConverterService(rates);

    {
        var response = new RatesResponse();
        response.putRatesItem(org.vl4ds4m.banking.converter.client.rates.model.Currency.RUB.getValue(), new BigDecimal("1"));
        response.putRatesItem(org.vl4ds4m.banking.converter.client.rates.model.Currency.USD.getValue(), new BigDecimal("35.47"));
        response.putRatesItem(org.vl4ds4m.banking.converter.client.rates.model.Currency.EUR.getValue(), new BigDecimal("50.2"));
        response.setBase(org.vl4ds4m.banking.converter.client.rates.model.Currency.RUB);
        Mockito.when(rates.getRatesResponse()).thenReturn(response);
    }

    @ParameterizedTest
    @MethodSource("provideConverterArguments")
    void convert(
        Currency source,
        Currency target,
        String amount,
        String result
    ) {
        BigDecimal actual = service.convert(
            source,
            target,
            new BigDecimal(amount)
        );
        assertEquals(new BigDecimal(result), actual);
    }

    static Stream<Arguments> provideConverterArguments() {
        return Stream.of(
            Arguments.of(Currency.EUR, Currency.RUB, "1.54", "77.31"),
            Arguments.of(Currency.RUB, Currency.USD, "78.1", "2.20"),
            Arguments.of(Currency.USD, Currency.EUR, "34.62", "24.46")
        );
    }

    @Test
    void tryConvertInaccessibleCurrency() {
        var inaccessible = Currency.CNY;
        var usd = Currency.USD;
        BigDecimal amount = BigDecimal.ONE;
        assertThrows(RatesServiceException.class,
            () -> service.convert(inaccessible, usd, amount));
        assertThrows(RatesServiceException.class,
            () -> service.convert(usd, inaccessible, amount));
    }

    @Test
    void tryConvertNonPositiveAmount() {
        var source = Currency.USD;
        var target = Currency.RUB;
        assertThrows(NonPositiveAmountException.class,
            () -> service.convert(source, target, BigDecimal.ZERO));
        assertThrows(NonPositiveAmountException.class,
            () -> service.convert(source, target, new BigDecimal("-4.54")));
    }
}
