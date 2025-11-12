package org.vl4ds4m.banking.accounts.client.grpc;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.vl4ds4m.banking.accounts.client.ConverterClient;
import org.vl4ds4m.banking.common.entity.Currency;
import org.vl4ds4m.banking.common.entity.Money;
import org.vl4ds4m.banking.converter.grpc.ConvertRequest;
import org.vl4ds4m.banking.converter.grpc.ConverterGrpc;

import java.math.BigDecimal;

@RequiredArgsConstructor
@Slf4j
public class ConverterGrpcClient implements ConverterClient {

    private final ConverterGrpc.ConverterBlockingStub grpcStub;

    @Override
    public Money convertCurrency(Currency from, Currency to, Money money) {
        var request = ConvertRequest.newBuilder()
                .setFrom(toGrpcCurrency(from))
                .setTo(toGrpcCurrency(to))
                .setAmount(money.amount().doubleValue())
                .build();

        log.info("Request currency conversion: {}, {} -> {}", money.amount(), from, to);
        var response = grpcStub.convert(request);

        var convertedAmount = BigDecimal.valueOf(response.getAmount());
        return Money.of(convertedAmount);
    }

    private static org.vl4ds4m.banking.common.grpc.Currency toGrpcCurrency(Currency currency) {
        var grpcName = "CURRENCY_" + currency.name();
        return org.vl4ds4m.banking.common.grpc.Currency.valueOf(grpcName);
    }
}
