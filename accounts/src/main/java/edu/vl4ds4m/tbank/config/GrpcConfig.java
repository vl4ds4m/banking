package edu.vl4ds4m.tbank.config;

import edu.vl4ds4m.tbank.converter.grpc.ConverterGrpc;
import edu.vl4ds4m.tbank.service.ConverterService;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.micrometer.observation.ObservationRegistry;
import net.devh.boot.grpc.client.inject.GrpcClient;
import net.devh.boot.grpc.client.inject.GrpcClientBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@GrpcClientBean(
        clazz = ConverterGrpc.ConverterBlockingStub.class,
        beanName = "grpcStub",
        client = @GrpcClient("accounts")
)
public class GrpcConfig {
    @Bean
    public ConverterService converterService(
            @Autowired ConverterGrpc.ConverterBlockingStub grpcStub,
            CircuitBreaker circuitBreaker,
            ObservationRegistry registry
    ) {
        return new ConverterService(grpcStub, circuitBreaker, registry);
    }
}
