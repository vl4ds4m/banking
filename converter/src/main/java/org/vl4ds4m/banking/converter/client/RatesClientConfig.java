package org.vl4ds4m.banking.converter.client;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.retry.RetryException;
import org.springframework.web.client.RestTemplate;
import org.vl4ds4m.banking.common.exception.ServiceException;
import org.vl4ds4m.banking.common.handler.retry.RetryTemplateFactory;
import org.vl4ds4m.banking.common.properties.RatesClientProperties;
import org.vl4ds4m.banking.rates.openapi.client.invoke.ApiClient;

import java.util.function.Supplier;

@Configuration
@EnableConfigurationProperties(RatesClientProperties.class)
@RequiredArgsConstructor
public class RatesClientConfig {

    private final RatesClientProperties ratesProps;

    private final RetryTemplateFactory retryTemplateFactory;

    @Bean
    public RatesClient ratesClient(RestTemplate restTemplate) {
        var client = new ApiClient(restTemplate);
        client.setBasePath(ratesProps.httpUrl());
        var impl = new RatesClientImpl(client);

        var retryTemplate = retryTemplateFactory.createRetryTemplate("rates");
        return new RatesDecorator(impl) {
            @Override
            protected <T> Supplier<T> decorateGetRates(Supplier<T> fn) {
                return () -> {
                    try {
                        return retryTemplate.execute(fn::get);
                    } catch (RetryException e) {
                        if (e.getCause() instanceof ServiceException c) {
                            throw c;
                        }
                        throw new RuntimeException(e);
                    }
                };
            }
        };
    }

}
