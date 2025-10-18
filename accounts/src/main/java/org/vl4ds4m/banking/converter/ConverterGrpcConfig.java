package org.vl4ds4m.banking.converter;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.micrometer.observation.ObservationRegistry;
import net.devh.boot.grpc.client.inject.GrpcClient;
import net.devh.boot.grpc.client.inject.GrpcClientBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.vl4ds4m.banking.converter.grpc.ConverterGrpc;

@Configuration(proxyBeanMethods = false)
@GrpcClientBean(
    clazz = ConverterGrpc.ConverterBlockingStub.class,
    beanName = "grpcStub",
    client = @GrpcClient("accounts"))
public class ConverterGrpcConfig {
    @Bean
    public ConverterService converterService(
        @Autowired
        ConverterGrpc.ConverterBlockingStub grpcStub,
        CircuitBreaker circuitBreaker,
        ObservationRegistry registry
    ) {
        return new ConverterService(grpcStub, circuitBreaker, registry);
    }
}
