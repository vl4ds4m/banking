package edu.tinkoff.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
class ConverterInvoker {

    @Value("${services.converter.url}")
    private String converterUrl;

    Map<String, Object> convert(String from, String to, double amount) {
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForObject(
                converterUrl + "?from={a}&to={b}&amount={c}",
                Map.class,
                from, to, amount
        );
    }
}
