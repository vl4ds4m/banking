package org.vl4ds4m.banking.accounts.transfer;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record TransferRequest(
    @JsonProperty("receiverAccount")
    @NotNull
    Integer receiverNumber,

    @JsonProperty("senderAccount")
    @NotNull
    Integer senderNumber,

    @JsonProperty("amountInSenderCurrency")
    @NotNull
    @Positive
    BigDecimal amount
) {
}
