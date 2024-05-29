package edu.tinkoff.config;

import io.grpc.ClientInterceptor;
import io.micrometer.core.instrument.binder.grpc.ObservationGrpcClientInterceptor;
import io.micrometer.observation.ObservationRegistry;
import net.devh.boot.grpc.client.interceptor.GrpcGlobalClientInterceptor;
import net.devh.boot.grpc.common.util.InterceptorOrder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

@Configuration
public class OtelTraceInterceptorConfig {
    @Bean
    @GrpcGlobalClientInterceptor
    @Order(InterceptorOrder.ORDER_TRACING_METRICS)
    public ClientInterceptor clientInterceptor(ObservationRegistry observationRegistry) {
        return new ObservationGrpcClientInterceptor(observationRegistry);
    }
}
