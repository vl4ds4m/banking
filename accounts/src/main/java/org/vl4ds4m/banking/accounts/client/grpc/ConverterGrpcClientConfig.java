package org.vl4ds4m.banking.accounts.client.grpc;

import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import net.devh.boot.grpc.client.inject.GrpcClientBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.vl4ds4m.banking.accounts.client.ConverterClientImpl;
import org.vl4ds4m.banking.common.properties.ConverterClientProperties;
import org.vl4ds4m.banking.converter.grpc.ConverterGrpc;

@Configuration
@ConditionalOnProperty(
        name = ConverterClientProperties.GRPC_PROP,
        havingValue = "true")
@GrpcClientBean(
        clazz = ConverterGrpc.ConverterBlockingStub.class,
        beanName = "converterGrpcStub",
        client = @GrpcClient("converter"))
@Slf4j
public class ConverterGrpcClientConfig {

    @Bean
    public ConverterClientImpl converterGrpcClient(ConverterGrpc.ConverterBlockingStub converterGrpcStub) {
        log.info("Create GRPC converter client");
        return new ConverterGrpcClient(converterGrpcStub);
    }
}
