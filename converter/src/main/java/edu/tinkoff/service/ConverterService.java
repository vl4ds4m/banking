package edu.tinkoff.service;

import edu.tinkoff.dto.CurrencyMessage;
import edu.tinkoff.dto.Currency;
import edu.tinkoff.dto.RatesResposne;
import edu.tinkoff.grpc.ConversionReply;
import edu.tinkoff.grpc.ConversionRequest;
import edu.tinkoff.grpc.ConverterServiceGrpc.ConverterServiceImplBase;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static edu.tinkoff.util.Conversions.SCALE;
import static edu.tinkoff.util.Conversions.ROUNDING_MODE;
import static edu.tinkoff.util.Conversions.setScale;

import java.math.BigDecimal;
import java.util.Map;

@GrpcService
public class ConverterService extends ConverterServiceImplBase {
    private static final Logger logger = LoggerFactory.getLogger(ConverterService.class);

    private final RatesService ratesService;

    public ConverterService(RatesService ratesService) {
        this.ratesService = ratesService;
    }

    @Override
    public void convert(ConversionRequest request, StreamObserver<ConversionReply> responseObserver) {
        try {
            CurrencyMessage message = convert(
                    request.getFrom(),
                    request.getTo(),
                    BigDecimal.valueOf(request.getAmount())
            );

            ConversionReply reply = ConversionReply.newBuilder()
                    .setCurrency(message.currency().toString())
                    .setAmount(message.amount().doubleValue())
                    .build();

            responseObserver.onNext(reply);
            responseObserver.onCompleted();
        } catch (RuntimeException e) {
            logger.error("Conversion exception: {}", e.getMessage());
            responseObserver.onError(Status.INTERNAL.asException());
        }
    }

    public CurrencyMessage convert(String fromName, String toName, BigDecimal amount) {
        Currency from = Currency.fromValue(fromName);
        if (from == null) {
            return currencyErrorResponse(fromName);
        }

        Currency to = Currency.fromValue(toName);
        if (to == null) {
            return currencyErrorResponse(toName);
        }

        if (BigDecimal.ZERO.compareTo(amount) >= 0) {
            return invalidAmountErrorResponse();
        }

        BigDecimal convertedAmount = convert(from, to, amount);
        return new CurrencyMessage(to, convertedAmount, null);
    }

    private CurrencyMessage currencyErrorResponse(String currencyName) {
        return new CurrencyMessage(
                null, null,
                "Валюта " + currencyName + " недоступна"
        );
    }

    private CurrencyMessage invalidAmountErrorResponse() {
        return new CurrencyMessage(
                null, null,
                "Отрицательная сумма"
        );
    }

    private BigDecimal convert(Currency from, Currency to, BigDecimal amount) {
        RatesResposne ratesResposne = ratesService.getRatesResponse();
        Map<String, BigDecimal> rates = ratesResposne.getRates();

        BigDecimal convertedAmount;

        if (from == to) {
            convertedAmount = setScale(amount);

        } else if (from == ratesResposne.getBase()) {
            BigDecimal currencyValue = rates.get(to.getValue());
            convertedAmount = amount.divide(currencyValue, SCALE, ROUNDING_MODE);

        } else if (to == ratesResposne.getBase()) {
            BigDecimal currencyValue = rates.get(from.getValue());
            convertedAmount = setScale(amount.multiply(currencyValue));

        } else {
            BigDecimal currencyValue = rates.get(from.getValue());
            convertedAmount = amount.multiply(currencyValue);
            currencyValue = rates.get(to.getValue());
            convertedAmount = convertedAmount.divide(currencyValue, SCALE, ROUNDING_MODE);
        }

        return convertedAmount;
    }
}
