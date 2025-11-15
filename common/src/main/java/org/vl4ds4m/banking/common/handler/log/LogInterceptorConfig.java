package org.vl4ds4m.banking.common.handler.log;

import net.devh.boot.grpc.client.interceptor.GrpcGlobalClientInterceptor;
import net.devh.boot.grpc.server.interceptor.GrpcGlobalServerInterceptor;
import org.springframework.boot.web.servlet.FilterRegistration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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

    @GrpcGlobalServerInterceptor
    public GrpcServerLogInterceptor grpcServerLog() {
        return new GrpcServerLogInterceptor();
    }

    @GrpcGlobalClientInterceptor
    public GrpcClientLogInterceptor grpcClientLog() {
        return new GrpcClientLogInterceptor();
    }
}
