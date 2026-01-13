package org.vl4ds4m.banking.accounts.client.grpc;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBooleanProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.grpc.client.GrpcChannelFactory;
import org.vl4ds4m.banking.accounts.client.ConverterClientImpl;
import org.vl4ds4m.banking.common.properties.ConverterClientProperties;
import org.vl4ds4m.banking.converter.grpc.ConverterGrpc;

@Configuration
@ConditionalOnBooleanProperty(
        name = ConverterClientProperties.GRPC_PROP)
@Slf4j
public class ConverterGrpcClientConfig {

    @Bean
    public ConverterGrpc.ConverterBlockingStub converterGrpcStub(GrpcChannelFactory channels) {
        return ConverterGrpc.newBlockingStub(channels.createChannel("converter"));
    }

    @Bean
    public ConverterClientImpl converterGrpcClient(ConverterGrpc.ConverterBlockingStub converterGrpcStub) {
        log.info("Create GRPC converter client");
        return new ConverterGrpcClient(converterGrpcStub);
    }

}
