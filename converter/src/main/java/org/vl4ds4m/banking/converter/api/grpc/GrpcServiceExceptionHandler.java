package org.vl4ds4m.banking.converter.api.grpc;

import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.advice.GrpcAdvice;
import org.slf4j.Logger;
import org.vl4ds4m.banking.common.handler.AbstractGrpcExceptionHandler;

@GrpcAdvice
@Slf4j
public class GrpcServiceExceptionHandler extends AbstractGrpcExceptionHandler {

    @Override
    protected Logger log() {
        return log;
    }
}
