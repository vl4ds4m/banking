package org.vl4ds4m.banking.converter.api.grpc;

import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;
import org.vl4ds4m.banking.common.util.To;
import org.vl4ds4m.banking.converter.grpc.ConvertRequest;
import org.vl4ds4m.banking.converter.grpc.ConvertResponse;
import org.vl4ds4m.banking.converter.grpc.ConverterGrpc;
import org.vl4ds4m.banking.converter.service.ConverterService;

import java.math.BigDecimal;

@GrpcService
@RequiredArgsConstructor
public class ConverterGrpcService extends ConverterGrpc.ConverterImplBase {

    private final ConverterService service;

    // TODO
    // @Observed
    @Override
    public void convert(ConvertRequest request, StreamObserver<ConvertResponse> observer) {
        var amount = BigDecimal.valueOf(request.getAmount());
        var converted = service.convert(
                To.currency(request.getFrom()),
                To.currency(request.getTo()),
                To.moneyOrReject(amount, "Amount"));

        var response = ConvertResponse.newBuilder()
                .setAmount(converted.amount().doubleValue())
                .build();

        observer.onNext(response);
        observer.onCompleted();
    }
}
