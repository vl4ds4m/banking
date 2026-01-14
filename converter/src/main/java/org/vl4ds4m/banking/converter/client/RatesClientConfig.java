package org.vl4ds4m.banking.converter.client;

import io.grpc.ClientInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.retry.RetryException;
import org.springframework.core.retry.RetryTemplate;
import org.springframework.grpc.client.ChannelBuilderOptions;
import org.springframework.grpc.client.GrpcChannelFactory;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.vl4ds4m.banking.common.handler.auth.OAuth2ClientGrpcInterceptor;
import org.vl4ds4m.banking.common.handler.exception.Exceptions;
import org.vl4ds4m.banking.common.handler.retry.RetryTemplateFactory;
import org.vl4ds4m.banking.converter.App;
import org.vl4ds4m.banking.rates.grpc.RatesGrpc;

import java.util.List;
import java.util.function.Supplier;

@Configuration
@RequiredArgsConstructor
public class RatesClientConfig {

    private final RetryTemplateFactory retryTemplateFactory;

    @Bean
    public RatesClient ratesClient(RatesGrpc.RatesBlockingStub ratesGrpcStub) {
        var impl = new RatesClientImpl(ratesGrpcStub);
        var retryTemplate = retryTemplateFactory.createRetryTemplate("rates");
        return decorateWithRetry(impl, retryTemplate);
    }

    @Bean
    public RatesGrpc.RatesBlockingStub ratesGrpcStub(
            GrpcChannelFactory channels,
            OAuth2ClientGrpcInterceptor oauth2Interceptor
    ) {
        List<ClientInterceptor> interceptors = List.of(oauth2Interceptor);
        ChannelBuilderOptions options = ChannelBuilderOptions.defaults()
                .withInterceptors(interceptors);
        return RatesGrpc.newBlockingStub(channels.createChannel("rates", options));
    }

    @Bean
    public OAuth2ClientGrpcInterceptor oauth2ClientGrpcInterceptor(
            ClientRegistrationRepository clientRegistrationRepository,
            OAuth2AuthorizedClientService authorizedClientService
    ) {
        return new OAuth2ClientGrpcInterceptor(
                clientRegistrationRepository,
                authorizedClientService,
                () -> App.OAUTH2_CLIENT_REG);
    }

    private static RatesClient decorateWithRetry(RatesClient impl, RetryTemplate template) {
        return new RatesDecorator(impl) {
            @Override
            protected <T> Supplier<T> decorateGetRates(Supplier<T> fn) {
                return () -> {
                    try {
                        return template.execute(fn::get);
                    } catch (RetryException e) {
                        return Exceptions.rethrow(e.getCause());
                    }
                };
            }
        };
    }

}
