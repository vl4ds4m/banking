package org.vl4ds4m.banking.common.handler.log;

import io.grpc.ForwardingServerCall.SimpleForwardingServerCall;
import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GrpcServerLogInterceptor implements ServerInterceptor {

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(
            ServerCall<ReqT, RespT> call,
            Metadata headers,
            ServerCallHandler<ReqT, RespT> next
    ) {
        var request = call.getMethodDescriptor().getFullMethodName();
        log.info("Accept GRPC request {}", request);

        var loggingCall = new LoggingServerCall<>(call, request);
        return next.startCall(loggingCall, headers);
    }

    private static class LoggingServerCall<ReqT, RespT> extends SimpleForwardingServerCall<ReqT, RespT> {

        final String request;

        LoggingServerCall(ServerCall<ReqT, RespT> delegate, String request) {
            super(delegate);
            this.request = request;
        }

        @Override
        public void sendMessage(RespT message) {
            log.info("GRPC request {} processed", request);
            super.sendMessage(message);
        }

    }

}
