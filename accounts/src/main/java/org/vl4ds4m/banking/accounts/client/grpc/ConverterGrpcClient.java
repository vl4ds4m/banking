package org.vl4ds4m.banking.accounts.client.grpc;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.vl4ds4m.banking.accounts.client.ConverterClient;
import org.vl4ds4m.banking.common.entity.Currency;
import org.vl4ds4m.banking.common.entity.Money;
import org.vl4ds4m.banking.common.util.To;
import org.vl4ds4m.banking.converter.grpc.ConvertRequest;
import org.vl4ds4m.banking.converter.grpc.ConverterGrpc;

import java.math.BigDecimal;

@RequiredArgsConstructor
@Slf4j
public class ConverterGrpcClient implements ConverterClient {

    private final ConverterGrpc.ConverterBlockingStub grpcStub;

    @Override
    public Money convertCurrency(Currency source, Currency target, Money money) {
        var request = ConvertRequest.newBuilder()
                .setFrom(To.currency(source))
                .setTo(To.currency(target))
                .setAmount(money.amount().doubleValue())
                .build();

        log.info("Request currency conversion: {}, {} -> {}", money.amount(), source, target);
        var response = grpcStub.convert(request);

        var convertedAmount = BigDecimal.valueOf(response.getAmount());
        return Money.of(convertedAmount);
    }
}
