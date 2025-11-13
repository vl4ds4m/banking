package org.vl4ds4m.banking.accounts.client;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.vl4ds4m.banking.accounts.client.grpc.ConverterGrpcClient;
import org.vl4ds4m.banking.accounts.client.http.ConverterHttpClient;
import org.vl4ds4m.banking.common.properties.ConverterProperties;
import org.vl4ds4m.banking.converter.client.http.invoker.ApiClient;
import org.vl4ds4m.banking.converter.grpc.ConverterGrpc;

@Configuration
@EnableConfigurationProperties(ConverterProperties.class)
@RequiredArgsConstructor
public class ConverterClientConfig {

    private final ConverterProperties converterProps;

    @Bean
    public ConverterClient converterClient(
            ConverterGrpc.ConverterBlockingStub converterGrpcStub,
            RestTemplate restTemplate
    ) {
        return converterProps.grpc()
                ? createGrpcClient(converterGrpcStub)
                : createHttpClient(restTemplate);
    }

    private ConverterGrpcClient createGrpcClient(ConverterGrpc.ConverterBlockingStub grpcStub) {
        return new ConverterGrpcClient(grpcStub);
    }

    private ConverterHttpClient createHttpClient(RestTemplate restTemplate) {
        var client = new ApiClient(restTemplate);
        client.setBasePath(converterProps.httpUrl());
        return new ConverterHttpClient(client);
    }
}
