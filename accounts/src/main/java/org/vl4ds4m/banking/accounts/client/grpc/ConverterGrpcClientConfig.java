package org.vl4ds4m.banking.accounts.client.grpc;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBooleanProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.grpc.client.GrpcChannelFactory;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.vl4ds4m.banking.accounts.App;
import org.vl4ds4m.banking.accounts.client.ConverterClientImpl;
import org.vl4ds4m.banking.common.handler.auth.OAuth2ClientGrpcInterceptor;
import org.vl4ds4m.banking.common.properties.ConverterClientProperties;
import org.vl4ds4m.banking.converter.grpc.ConverterGrpc;

@Configuration
@ConditionalOnBooleanProperty(
        name = ConverterClientProperties.GRPC_PROP)
@Slf4j
public class ConverterGrpcClientConfig {

    @Bean
    public ConverterGrpc.ConverterBlockingStub converterGrpcStub(
            GrpcChannelFactory channels,
            OAuth2AuthorizedClientManager authorizedClientManager
    ) {
        return ConverterGrpc.newBlockingStub(channels.createChannel("converter"))
                .withInterceptors(createOAuth2Interceptor(authorizedClientManager));
    }

    @Bean
    public ConverterClientImpl converterGrpcClient(ConverterGrpc.ConverterBlockingStub converterGrpcStub) {
        log.info("Create GRPC converter client");
        return new ConverterGrpcClient(converterGrpcStub);
    }

    private static OAuth2ClientGrpcInterceptor createOAuth2Interceptor(
            OAuth2AuthorizedClientManager authorizedClientManager
    ) {
        return new OAuth2ClientGrpcInterceptor(authorizedClientManager, () -> App.OAUTH2_CLIENT_REG);
    }

}
