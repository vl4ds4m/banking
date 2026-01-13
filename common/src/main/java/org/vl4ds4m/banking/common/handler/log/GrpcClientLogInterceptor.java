package org.vl4ds4m.banking.common.handler.log;

import io.grpc.*;
import io.grpc.ForwardingClientCall.SimpleForwardingClientCall;
import io.grpc.ForwardingClientCallListener.SimpleForwardingClientCallListener;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GrpcClientLogInterceptor implements ClientInterceptor {

    @Override
    public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(
            MethodDescriptor<ReqT, RespT> method,
            CallOptions callOptions,
            Channel next
    ) {
        var request = method.getFullMethodName();
        log.info("Send GRPC request {}", request);

        ClientCall<ReqT, RespT> loggedCall = next.newCall(method, callOptions);
        return new LoggingClientCall<>(loggedCall, request);
    }

    private static class LoggingClientCall<ReqT, RespT> extends SimpleForwardingClientCall<ReqT, RespT> {

        final String request;

        LoggingClientCall(ClientCall<ReqT, RespT> delegate, String request) {
            super(delegate);
            this.request = request;
        }

        @Override
        public void start(Listener<RespT> responseListener, Metadata headers) {
            var loggingListener = new LoggingClientCallListener<>(responseListener, request);
            super.start(loggingListener, headers);
        }

    }

    private static class LoggingClientCallListener<RespT> extends SimpleForwardingClientCallListener<RespT> {

        final String request;

        LoggingClientCallListener(ClientCall.Listener<RespT> delegate, String request) {
            super(delegate);
            this.request = request;
        }

        @Override
        public void onMessage(RespT message) {
            log.info("Receive GRPC response {}", request);
            super.onMessage(message);
        }

    }

}
