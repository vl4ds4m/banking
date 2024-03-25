package edu.tinkoff.service;

import edu.tinkoff.dto.CurrencyMessage;
import edu.tinkoff.model.Currency;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Objects;

@Service
public class ConverterService {
    private final RestTemplate restTemplate;
    private String converterUrl;

    public ConverterService(@Qualifier("auth") RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Value("${services.converter.url}")
    public void setConverterUrl(String converterUrl) {
        this.converterUrl = converterUrl;
    }

    public BigDecimal convert(Currency from, Currency to, BigDecimal amount) {
        CurrencyMessage message = restTemplate.getForObject(
                converterUrl,
                CurrencyMessage.class,
                from, to, amount
        );
        return Objects.requireNonNull(message).amount();
    }
}
