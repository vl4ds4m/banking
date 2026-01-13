package org.vl4ds4m.banking.common.config;

import io.grpc.ClientInterceptor;
import io.grpc.ServerInterceptor;
import io.micrometer.observation.ObservationRegistry;
import io.micrometer.observation.aop.ObservedAspect;
import org.springframework.context.annotation.Bean;

// TODO
// @Configuration
public class ObservationConfig {

    @Bean
    public ObservedAspect observedAspect(ObservationRegistry registry) {
        return new ObservedAspect(registry);
    }

    @Bean
    //@GrpcGlobalClientInterceptor
    //@Order(InterceptorOrder.ORDER_TRACING_METRICS)
    public ClientInterceptor clientInterceptor(ObservationRegistry observationRegistry) {
        return null; // micrometer-core
        // return new ObservationGrpcClientInterceptor(observationRegistry);
    }

    @Bean
    //@GrpcGlobalServerInterceptor
    //@Order(InterceptorOrder.ORDER_GLOBAL_EXCEPTION_HANDLING)
    public ServerInterceptor observationGrpcInterceptor(ObservationRegistry observationRegistry) {
        return null; // micrometer-core
        // return new ObservationGrpcServerInterceptor(observationRegistry);
    }

    // void decorateAction(ObservationRegistry registry) {
    //     Observation.createNotStarted("observation_name", registry)
    //             .observe(() -> System.out.println("Do something"));
    // }
}
