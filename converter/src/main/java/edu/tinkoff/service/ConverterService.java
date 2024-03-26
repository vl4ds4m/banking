package edu.tinkoff.service;

import edu.tinkoff.dto.CurrencyMessage;
import edu.tinkoff.dto.Currency;
import edu.tinkoff.dto.RatesResposne;
import org.springframework.stereotype.Service;

import static edu.tinkoff.util.Conversions.SCALE;
import static edu.tinkoff.util.Conversions.ROUNDING_MODE;
import static edu.tinkoff.util.Conversions.setScale;

import java.math.BigDecimal;
import java.util.Map;

@Service
public class ConverterService {
    private final RatesService ratesService;

    public ConverterService(RatesService ratesService) {
        this.ratesService = ratesService;
    }

    public CurrencyMessage convert(String fromName, String toName, BigDecimal amount) {
        Currency from = Currency.fromValue(fromName);
        if (from == null) {
            return currencyErrorResponse(fromName);
        }

        Currency to = Currency.fromValue(toName);
        if (to == null) {
            return currencyErrorResponse(toName);
        }

        if (BigDecimal.ZERO.compareTo(amount) >= 0) {
            return invalidAmountErrorResponse();
        }

        BigDecimal convertedAmount = convert(from, to, amount);
        return new CurrencyMessage(to, convertedAmount, null);
    }

    private CurrencyMessage currencyErrorResponse(String currencyName) {
        return new CurrencyMessage(
                null, null,
                "Валюта " + currencyName + " недоступна"
        );
    }

    private CurrencyMessage invalidAmountErrorResponse() {
        return new CurrencyMessage(
                null, null,
                "Отрицательная сумма"
        );
    }

    private BigDecimal convert(Currency from, Currency to, BigDecimal amount) {
        RatesResposne ratesResposne = ratesService.getRatesResponse();
        Map<String, BigDecimal> rates = ratesResposne.getRates();

        BigDecimal convertedAmount;

        if (from == to) {
            convertedAmount = setScale(amount);

        } else if (from == ratesResposne.getBase()) {
            BigDecimal currencyValue = rates.get(to.getValue());
            convertedAmount = amount.divide(currencyValue, SCALE, ROUNDING_MODE);

        } else if (to == ratesResposne.getBase()) {
            BigDecimal currencyValue = rates.get(from.getValue());
            convertedAmount = setScale(amount.multiply(currencyValue));

        } else {
            BigDecimal currencyValue = rates.get(from.getValue());
            convertedAmount = amount.multiply(currencyValue);
            currencyValue = rates.get(to.getValue());
            convertedAmount = convertedAmount.divide(currencyValue, SCALE, ROUNDING_MODE);
        }

        return convertedAmount;
    }
}
