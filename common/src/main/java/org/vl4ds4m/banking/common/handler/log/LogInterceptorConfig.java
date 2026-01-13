package org.vl4ds4m.banking.common.handler.log;

import org.springframework.boot.web.servlet.FilterRegistration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.grpc.client.GlobalClientInterceptor;
import org.springframework.grpc.server.GlobalServerInterceptor;

@Configuration
public class LogInterceptorConfig {

    @Bean
    @FilterRegistration
    public HttpServerLogFilter httpServerLog() {
        return new HttpServerLogFilter();
    }

    @Bean
    public HttpClientLogInterceptor httpClientLog() {
        return new HttpClientLogInterceptor();
    }

    @Bean
    @GlobalServerInterceptor
    public GrpcServerLogInterceptor grpcServerLog() {
        return new GrpcServerLogInterceptor();
    }

    @Bean
    @GlobalClientInterceptor
    public GrpcClientLogInterceptor grpcClientLog() {
        return new GrpcClientLogInterceptor();
    }
}
