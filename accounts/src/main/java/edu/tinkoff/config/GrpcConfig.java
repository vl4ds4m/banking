package edu.tinkoff.config;

import edu.tinkoff.grpc.ConverterServiceGrpc;
import edu.tinkoff.service.ConverterService;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.micrometer.observation.ObservationRegistry;
import net.devh.boot.grpc.client.inject.GrpcClient;
import net.devh.boot.grpc.client.inject.GrpcClientBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@GrpcClientBean(
        clazz = ConverterServiceGrpc.ConverterServiceBlockingStub.class,
        beanName = "grpcStub",
        client = @GrpcClient("accounts")
)
public class GrpcConfig {
    @Bean
    public ConverterService converterService(
            @Autowired ConverterServiceGrpc.ConverterServiceBlockingStub grpcStub,
            CircuitBreaker circuitBreaker,
            ObservationRegistry registry
    ) {
        return new ConverterService(grpcStub, circuitBreaker, registry);
    }
}
