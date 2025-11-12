package org.vl4ds4m.banking.accounts.client.grpc;

import net.devh.boot.grpc.client.inject.GrpcClient;
import net.devh.boot.grpc.client.inject.GrpcClientBean;
import org.springframework.context.annotation.Configuration;
import org.vl4ds4m.banking.converter.grpc.ConverterGrpc;

@Configuration
@GrpcClientBean(
        clazz = ConverterGrpc.ConverterBlockingStub.class,
        beanName = "converterGrpcStub",
        client = @GrpcClient("converter"))
public class ConverterGrpcConfig {}
