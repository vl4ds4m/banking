package org.vl4ds4m.banking.common.handler.log;

import org.springframework.boot.web.servlet.FilterRegistration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.grpc.client.GlobalClientInterceptor;
import org.springframework.grpc.server.GlobalServerInterceptor;

@Configuration
public class LogInterceptorConfig {

    @Bean
    @FilterRegistration(order = Ordered.HIGHEST_PRECEDENCE)
    public HttpServerLogFilter httpServerLog() {
        return new HttpServerLogFilter();
    }

    @Bean
    public HttpClientLogInterceptor httpClientLog() {
        return new HttpClientLogInterceptor();
    }

    @Bean
    @GlobalServerInterceptor
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public GrpcServerLogInterceptor grpcServerLog() {
        return new GrpcServerLogInterceptor();
    }

    @Bean
    @GlobalClientInterceptor
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public GrpcClientLogInterceptor grpcClientLog() {
        return new GrpcClientLogInterceptor();
    }

}
