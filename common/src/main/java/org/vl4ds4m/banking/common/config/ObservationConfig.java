package org.vl4ds4m.banking.common.config;

import io.grpc.ClientInterceptor;
import io.micrometer.observation.ObservationRegistry;
import io.micrometer.observation.aop.ObservedAspect;
import net.devh.boot.grpc.client.interceptor.GrpcGlobalClientInterceptor;
import net.devh.boot.grpc.common.util.InterceptorOrder;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;

// TODO
// @Configuration
public class ObservationConfig {

    @Bean
    public ObservedAspect observedAspect(ObservationRegistry registry) {
        return new ObservedAspect(registry);
    }

    @Bean
    @GrpcGlobalClientInterceptor
    @Order(InterceptorOrder.ORDER_TRACING_METRICS)
    public ClientInterceptor clientInterceptor(ObservationRegistry observationRegistry) {
        return null;
        // micrometer-core
        // return new ObservationGrpcClientInterceptor(observationRegistry);
    }

    // void decorateAction(ObservationRegistry registry) {
    //     Observation.createNotStarted("observation_name", registry)
    //             .observe(() -> System.out.println("Do something"));
    // }
}
