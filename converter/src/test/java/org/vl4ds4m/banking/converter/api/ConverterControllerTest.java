package org.vl4ds4m.banking.converter.api;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.vl4ds4m.banking.converter.api.model.Currency;
import org.vl4ds4m.banking.converter.service.exception.NonPositiveAmountException;
import org.vl4ds4m.banking.converter.service.ConverterService;

import java.math.BigDecimal;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ConverterController.class)
@Import(SecurityConfiguration.class)
class ConverterControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ConverterService service;

    @ParameterizedTest
    @MethodSource("provideConverterArguments")
    void convert(
        Currency source,
        Currency target,
        BigDecimal amount,
        BigDecimal result
    ) throws Exception {
        String src = source.getValue();
        String tgt = target.getValue();
        when(service.convert(source, target, amount)).thenReturn(result);
        doRequest(src, tgt, amount).andDo(
            print()
        ).andExpect(
            status().isOk()
        ).andExpect(
            content().string(containsString(result.toString()))
        );
    }

    static Stream<Arguments> provideConverterArguments() {
        return Stream.of(
            Arguments.of(Currency.EUR, Currency.RUB, "1.54", "65.73"),
            Arguments.of(Currency.RUB, Currency.USD, "78.1", "2.17"),
            Arguments.of(Currency.CNY, Currency.RUB, "1", "2")
        );
    }

    @Test
    void tryConvertNegativeAmount() throws Exception {
        var src = Currency.CNY;
        var tgt = Currency.GBP;
        BigDecimal amount = new BigDecimal("-234.34");
        when(service.convert(src, tgt, amount)).thenThrow(NonPositiveAmountException.class);
        doRequest(src.getValue(), tgt.getValue(), amount).andDo(
            print()
        ).andExpect(
            status().isBadRequest()
        ).andExpect(
            content().string(containsString("Отрицательная сумма"))
        );
    }

    @Test
    void tryConvertInvalidCurrency() throws Exception {
        String src = Currency.EUR.getValue();
        String tgt = "Magic dollars";
        BigDecimal amount = new BigDecimal("834.34");
        doRequest(src, tgt, amount).andDo(
            print()
        ).andExpect(
            status().isBadRequest()
        );
    }

    private ResultActions doRequest(
        String source,
        String target,
        BigDecimal amount
    ) throws Exception {
        return mockMvc.perform(
            get("/convert")
                .queryParam("from", source)
                .queryParam("to", target)
                .queryParam("amount", amount.toString())
        );
    }
}
