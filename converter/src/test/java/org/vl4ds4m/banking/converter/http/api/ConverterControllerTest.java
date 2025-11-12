package org.vl4ds4m.banking.converter.http.api;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.vl4ds4m.banking.common.entity.Money;
import org.vl4ds4m.banking.converter.http.api.converter.CurrencyConverter;
import org.vl4ds4m.banking.converter.http.api.model.Currency;
import org.vl4ds4m.banking.converter.service.ConverterService;

import java.math.BigDecimal;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ConverterController.class)
@Import(SecurityConfiguration.class)
class ConverterControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ConverterService converterService;

    @DisplayName("Конвертация денег")
    @ParameterizedTest(name = "{0} {2} -> {1} {3}")
    @MethodSource("provideConverterArguments")
    void testConversion(
        Currency source,
        Currency target,
        BigDecimal amount,
        BigDecimal expected
    ) throws Exception {
        // Arrange
        var src = source.getValue();
        var tgt = target.getValue();
        when(converterService.convert(
            CurrencyConverter.toEntity(source),
            CurrencyConverter.toEntity(target),
            Money.of(amount))
        ).thenReturn(Money.of(expected));

        // Act
        var result = doRequest(src, tgt, amount.toString());

        // Assert
        result.andExpect(status().isOk())
              .andExpect(content().string(containsString("" + expected)));
    }

    @DisplayName("Ошибка при конвертации недопустимой валюты")
    @ParameterizedTest(name = "{0} -> {1}")
    @MethodSource("provideInvalidCurrencies")
    void testConversionInvalidCurrencyFailed(String src, String tgt) throws Exception {
        // Act
        var result = doRequest(src, tgt, "834.34");

        // Assert
        result.andExpect(status().isBadRequest());
    }

    @DisplayName("Ошибка при получении денег для конвертации")
    @ParameterizedTest(name = "{0}")
    @ValueSource(strings = {"-234.34", "I0s.oo"})
    void testConversionInvalidAmountFailed(String amount) throws Exception {
        // Act
        var result = doRequest(Currency.EUR.getValue(), Currency.RUB.getValue(), amount);

        // Assert
        result.andExpect(status().isBadRequest());
    }

    private static Stream<Arguments> provideConverterArguments() {
        return Stream.of(
            Arguments.of(Currency.EUR, Currency.RUB, "1.54", "65.73"),
            Arguments.of(Currency.RUB, Currency.USD, "78.1", "2.17"),
            Arguments.of(Currency.CNY, Currency.RUB, "1", "2")
        );
    }

    private static Stream<Arguments> provideInvalidCurrencies() {
        var valid = Currency.EUR.getValue();
        var invalid = "Magic dollars";
        return Stream.of(
                Arguments.of(valid, invalid),
                Arguments.of(invalid, valid),
                Arguments.of(invalid, invalid)
        );
    }

    private ResultActions doRequest(
        String source,
        String target,
        String amount
    ) throws Exception {
        return mockMvc.perform(
            get("/convert")
                .queryParam("from", source)
                .queryParam("to", target)
                .queryParam("amount", amount)
        );
    }
}
