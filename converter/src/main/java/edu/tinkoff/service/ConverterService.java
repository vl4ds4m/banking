package edu.tinkoff.service;

import edu.tinkoff.model.Currency;
import edu.tinkoff.model.RatesResposne;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

@Service
public class ConverterService {
    private final RatesService ratesService;

    public ConverterService(RatesService ratesService) {
        this.ratesService = ratesService;
    }

    public BigDecimal convert(Currency from, Currency to, BigDecimal amount) {
        RatesResposne ratesResposne = ratesService.getRatesResponse();
        Map<String, BigDecimal> rates = ratesResposne.getRates();

        final int scale = 2;
        final RoundingMode roundingMode = RoundingMode.HALF_EVEN;

        BigDecimal convertedAmount;

        if (from == to) {
            convertedAmount = amount.setScale(scale, roundingMode);
        } else if (from == ratesResposne.getBase()) {
            BigDecimal currencyValue = rates.get(to.getValue());
            convertedAmount = amount.divide(currencyValue, scale, roundingMode);
        } else if (to == ratesResposne.getBase()) {
            BigDecimal currencyValue = rates.get(from.getValue());
            convertedAmount = amount.multiply(currencyValue).setScale(scale, roundingMode);
        } else {
            BigDecimal currencyValue = rates.get(from.getValue());
            convertedAmount = amount.multiply(currencyValue);
            currencyValue = rates.get(to.getValue());
            convertedAmount = convertedAmount.divide(currencyValue, scale, roundingMode);
        }

        return convertedAmount;
    }
}
