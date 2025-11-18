package org.vl4ds4m.banking.converter.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.vl4ds4m.banking.common.entity.Currency;
import org.vl4ds4m.banking.common.entity.Money;
import org.vl4ds4m.banking.common.exception.ServiceException;
import org.vl4ds4m.banking.converter.client.RatesClient;
import org.vl4ds4m.banking.converter.entity.CurrencyRates;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ConverterServiceTest {

    @DisplayName("Конвертация денег")
    @ParameterizedTest(name = "{0} {2} -> {1} {3}")
    @MethodSource("provideConverterArguments")
    void testConversion(
        Currency source,
        Currency target,
        BigDecimal amount,
        BigDecimal result
    ) {
        // Arrange
        var service = new ConverterService(mockRatesClient());

        // Act
        var actual = service.convert(source, target, Money.of(amount));

        // Assert
        assertEquals(Money.of(result), actual);
    }

    @DisplayName("Ошибка конвертации, когда информация о валюте отсутствует или курс = 0")
    @ParameterizedTest(name = "{0}")
    @EnumSource(value = Currency.class, names = {"CNY", "GBP"})
    void testConversionInaccessibleCurrencyFailed(Currency inaccessible) {
        // Arrange
        var usd = Currency.USD;
        var money = Money.of(BigDecimal.ONE);

        var service = new ConverterService(mockRatesClient());

        // Act & Assert
        assertThrows(ServiceException.class,
            () -> service.convert(inaccessible, usd, money));
        assertThrows(ServiceException.class,
            () -> service.convert(usd, inaccessible, money));
    }

    private static Stream<Arguments> provideConverterArguments() {
        return Stream.of(
            Arguments.of(Currency.EUR, Currency.RUB, "1.54", "77.31"),
            Arguments.of(Currency.RUB, Currency.USD, "78.1", "2.20"),
            Arguments.of(Currency.USD, Currency.EUR, "34.62", "24.46"),
            Arguments.of(Currency.EUR, Currency.RUB, "0.00", "0.00"),
            Arguments.of(Currency.CNY, Currency.CNY, "5743.04", "5743.04")
        );
    }

    private static RatesClient mockRatesClient() {
        var rates = new HashMap<Currency, Money>();
        rates.put(Currency.RUB, Money.of(new BigDecimal("1")));
        rates.put(Currency.USD, Money.of(new BigDecimal("35.47")));
        rates.put(Currency.EUR, Money.of(new BigDecimal("50.2")));
        rates.put(Currency.GBP, Money.empty());

        var currencyRates = new CurrencyRates(Currency.RUB, rates);

        var service = mock(RatesClient.class);
        when(service.getRates()).thenReturn(currencyRates);

        return service;
    }
}
