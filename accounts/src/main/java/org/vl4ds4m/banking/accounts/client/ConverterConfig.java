package org.vl4ds4m.banking.accounts.client;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.vl4ds4m.banking.accounts.client.grpc.ConverterGrpcClient;
import org.vl4ds4m.banking.accounts.client.http.ConverterHttpClient;
import org.vl4ds4m.banking.common.properties.ConverterProperties;
import org.vl4ds4m.banking.converter.grpc.ConverterGrpc;
import org.vl4ds4m.banking.converter.http.client.invoker.ApiClient;

@Configuration
@EnableConfigurationProperties(ConverterProperties.class)
@RequiredArgsConstructor
public class ConverterConfig {

    private final ConverterProperties properties;

    @Bean
    public ConverterClient converterClient(
            ConverterGrpc.ConverterBlockingStub converterGrpcStub,
            RestTemplate restTemplate
    ) {
        return properties.grpc()
                ? createGrpcClient(converterGrpcStub)
                : createHttpClient(restTemplate);
    }

    private ConverterGrpcClient createGrpcClient(ConverterGrpc.ConverterBlockingStub grpcStub) {
        return new ConverterGrpcClient(grpcStub);
    }

    private ConverterHttpClient createHttpClient(RestTemplate restTemplate) {
        var client = new ApiClient(restTemplate);
        client.setBasePath("http://" + properties.address());
        return new ConverterHttpClient(client);
    }
}
