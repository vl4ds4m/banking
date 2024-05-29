package edu.tinkoff.service;

import edu.tinkoff.dto.CurrencyMessage;
import edu.tinkoff.dto.Currency;
import edu.tinkoff.dto.RatesResposne;
import edu.tinkoff.grpc.ConversionReply;
import edu.tinkoff.grpc.ConversionRequest;
import edu.tinkoff.grpc.ConverterServiceGrpc.ConverterServiceImplBase;
import edu.tinkoff.util.Conversions;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import io.micrometer.observation.annotation.Observed;
import net.devh.boot.grpc.server.service.GrpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.Map;

@GrpcService
public class ConverterService extends ConverterServiceImplBase {
    private static final Logger log = LoggerFactory.getLogger(ConverterService.class);

    private final RatesService ratesService;

    public ConverterService(RatesService ratesService) {
        this.ratesService = ratesService;
    }

    @Observed
    @Override
    public void convert(ConversionRequest request, StreamObserver<ConversionReply> responseObserver) {
        log.info("Accept a request to convert currency");
        try {
            CurrencyMessage message = convert(
                    request.getFrom(),
                    request.getTo(),
                    request.getAmount());

            ConversionReply reply = ConversionReply.newBuilder()
                    .setCurrency(message.currency().toString())
                    .setAmount(message.amount().doubleValue())
                    .build();

            responseObserver.onNext(reply);
            responseObserver.onCompleted();
        } catch (RuntimeException e) {
            log.error("Conversion exception: {}", e.getMessage());
            responseObserver.onError(Status.INTERNAL.asException());
        }
    }

    @Observed
    public CurrencyMessage convert(String fromName, String toName, double amount) {
        Currency from = Currency.fromValue(fromName);
        if (from == null) {
            return currencyErrorResponse(fromName);
        }

        Currency to = Currency.fromValue(toName);
        if (to == null) {
            return currencyErrorResponse(toName);
        }

        BigDecimal initialAmount = Conversions.setScale(amount);
        if (BigDecimal.ZERO.compareTo(initialAmount) >= 0) {
            return invalidAmountErrorResponse();
        }

        BigDecimal convertedAmount = convert(from, to, initialAmount);

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
            convertedAmount = amount;

        } else if (from == ratesResposne.getBase()) {
            BigDecimal currencyValue = rates.get(to.getValue());
            convertedAmount = amount.divide(
                    currencyValue, Conversions.SCALE, Conversions.ROUNDING_MODE);

        } else if (to == ratesResposne.getBase()) {
            BigDecimal currencyValue = rates.get(from.getValue());
            convertedAmount = Conversions.setScale(amount.multiply(currencyValue));

        } else {
            BigDecimal currencyValue = rates.get(from.getValue());
            convertedAmount = amount.multiply(currencyValue);
            currencyValue = rates.get(to.getValue());
            convertedAmount = convertedAmount.divide(
                    currencyValue, Conversions.SCALE, Conversions.ROUNDING_MODE);
        }

        log.info("Convert [{} {}] to [{} {}]", amount, from, convertedAmount, to);
        return convertedAmount;
    }
}
