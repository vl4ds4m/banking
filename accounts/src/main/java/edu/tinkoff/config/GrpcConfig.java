package edu.tinkoff.config;

import edu.tinkoff.grpc.ConverterServiceGrpc.ConverterServiceBlockingStub;
import net.devh.boot.grpc.client.inject.GrpcClient;
import net.devh.boot.grpc.client.inject.GrpcClientBean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@GrpcClientBean(
        clazz = ConverterServiceBlockingStub.class,
        beanName = "grpcStub",
        client = @GrpcClient("accounts")
)
public class GrpcConfig {
}
