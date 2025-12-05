package org.vl4ds4m.banking.rates.api;

import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;
import org.vl4ds4m.banking.common.entity.CurrencyRates;
import org.vl4ds4m.banking.common.util.To;
import org.vl4ds4m.banking.rates.grpc.RatesGrpc;
import org.vl4ds4m.banking.rates.grpc.RatesMap;
import org.vl4ds4m.banking.rates.grpc.RatesRequest;
import org.vl4ds4m.banking.rates.grpc.RatesResponse;
import org.vl4ds4m.banking.rates.service.RatesService;

import java.util.List;

@GrpcService
@RequiredArgsConstructor
public class RatesGrpcService extends RatesGrpc.RatesImplBase {

    private final RatesService ratesService;

    @Override
    public void getRates(RatesRequest request, StreamObserver<RatesResponse> responseObserver) {
        CurrencyRates currencyRates = ratesService.getRates();

        List<RatesMap> ratesMapList = currencyRates.rates()
                .entrySet()
                .stream()
                .map(e -> RatesMap.newBuilder()
                        .setCurrency(To.grpcCurrency(e.getKey()))
                        .setRate(e.getValue().amount().doubleValue())
                        .build())
                .toList();

        var response = RatesResponse.newBuilder()
                .setBase(To.grpcCurrency(currencyRates.base()))
                .addAllRates(ratesMapList)
                .build();


        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
