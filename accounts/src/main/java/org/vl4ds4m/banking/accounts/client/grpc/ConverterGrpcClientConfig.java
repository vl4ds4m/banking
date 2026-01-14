package org.vl4ds4m.banking.accounts.client.grpc;

import io.grpc.ClientInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBooleanProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.grpc.client.ChannelBuilderOptions;
import org.springframework.grpc.client.GrpcChannelFactory;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.vl4ds4m.banking.accounts.App;
import org.vl4ds4m.banking.accounts.client.ConverterClientImpl;
import org.vl4ds4m.banking.common.handler.auth.OAuth2ClientGrpcInterceptor;
import org.vl4ds4m.banking.common.properties.ConverterClientProperties;
import org.vl4ds4m.banking.converter.grpc.ConverterGrpc;

import java.util.List;

@Configuration
@ConditionalOnBooleanProperty(
        name = ConverterClientProperties.GRPC_PROP)
@Slf4j
public class ConverterGrpcClientConfig {

    @Bean
    public ConverterClientImpl converterGrpcClient(ConverterGrpc.ConverterBlockingStub converterGrpcStub) {
        log.info("Create GRPC converter client");
        return new ConverterGrpcClient(converterGrpcStub);
    }

    @Bean
    public ConverterGrpc.ConverterBlockingStub converterGrpcStub(
            GrpcChannelFactory channels,
            OAuth2ClientGrpcInterceptor oauth2Interceptor
    ) {
        List<ClientInterceptor> interceptors = List.of(oauth2Interceptor);
        ChannelBuilderOptions options = ChannelBuilderOptions.defaults()
                .withInterceptors(interceptors);
        return ConverterGrpc.newBlockingStub(channels.createChannel("converter", options));
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

}
