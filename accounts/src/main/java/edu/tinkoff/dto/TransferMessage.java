package edu.tinkoff.dto;

import java.math.BigDecimal;

public record TransferMessage(
        Integer receiverAccount,
        Integer senderAccount,
        BigDecimal amountInSenderCurrency
) {
}
