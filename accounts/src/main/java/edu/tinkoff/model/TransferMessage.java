package edu.tinkoff.model;

import java.math.BigDecimal;

public record TransferMessage(
        Integer receiverAccount,
        Integer senderAccount,
        BigDecimal amountInSenderCurrency
) {
}
