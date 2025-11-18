package org.vl4ds4m.banking.accounts.client.grpc;

import io.grpc.StatusRuntimeException;
import lombok.RequiredArgsConstructor;
import org.vl4ds4m.banking.accounts.client.ConverterClient;
import org.vl4ds4m.banking.common.entity.Currency;
import org.vl4ds4m.banking.common.entity.Money;
import org.vl4ds4m.banking.common.exception.ServiceException;
import org.vl4ds4m.banking.common.util.To;
import org.vl4ds4m.banking.converter.grpc.ConvertRequest;
import org.vl4ds4m.banking.converter.grpc.ConvertResponse;
import org.vl4ds4m.banking.converter.grpc.ConverterGrpc;

import java.math.BigDecimal;

@RequiredArgsConstructor
public class ConverterGrpcClient implements ConverterClient {

    private final ConverterGrpc.ConverterBlockingStub grpcStub;

    @Override
    public Money convertCurrency(Currency source, Currency target, Money money) {
        var request = ConvertRequest.newBuilder()
                .setFrom(To.grpcCurrency(source))
                .setTo(To.grpcCurrency(target))
                .setAmount(money.amount().doubleValue())
                .build();

        ConvertResponse response;
        try {
            response = grpcStub.convert(request);
        } catch (StatusRuntimeException e) {
            throw new ServiceException("converter", e);
        }

        var convertedAmount = BigDecimal.valueOf(response.getAmount());
        return Money.of(convertedAmount);
    }
}
