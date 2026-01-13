package org.vl4ds4m.banking.converter.client;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.retry.RetryException;
import org.springframework.core.retry.RetryTemplate;
import org.springframework.grpc.client.GrpcChannelFactory;
import org.vl4ds4m.banking.common.exception.ServiceException;
import org.vl4ds4m.banking.common.handler.retry.RetryTemplateFactory;
import org.vl4ds4m.banking.rates.grpc.RatesGrpc;

import java.util.function.Supplier;

@Configuration
@RequiredArgsConstructor
public class RatesClientConfig {

    private final RetryTemplateFactory retryTemplateFactory;

    @Bean
    public RatesGrpc.RatesBlockingStub ratesGrpcStub(GrpcChannelFactory channels) {
        return RatesGrpc.newBlockingStub(channels.createChannel("rates"));
    }

    @Bean
    public RatesClient ratesClient(RatesGrpc.RatesBlockingStub ratesGrpcStub) {
        var impl = new RatesClientImpl(ratesGrpcStub);
        var retryTemplate = retryTemplateFactory.createRetryTemplate("rates");
        return decorateWithRetry(impl, retryTemplate);
    }

    private RatesClient decorateWithRetry(RatesClient impl, RetryTemplate template) {
        return new RatesDecorator(impl) {
            @Override
            protected <T> Supplier<T> decorateGetRates(Supplier<T> fn) {
                return () -> {
                    try {
                        return template.execute(fn::get);
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
