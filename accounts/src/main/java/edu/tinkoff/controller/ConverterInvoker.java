package edu.tinkoff.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
class ConverterInvoker {

    @Value("${services.converter.url}")
    private String converterUrl;

    @Autowired
    private RestTemplate restTemplate;

    Map<String, Object> convert(String from, String to, double amount) {
        return restTemplate.getForObject(
                converterUrl + "?from={a}&to={b}&amount={c}",
                Map.class,
                from, to, amount
        );
    }
}
