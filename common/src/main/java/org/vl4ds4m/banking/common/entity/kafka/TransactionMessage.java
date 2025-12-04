package org.vl4ds4m.banking.common.entity.kafka;

import org.vl4ds4m.banking.common.entity.Transaction;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record TransactionMessage(

    UUID transactionId,

    long accountNumber,

    BigDecimal amount,

    boolean withdraw,

    Instant timestamp

) {

    public static final String KAFKA_TOPIC = "transactions";

    public static TransactionMessage create(Transaction t, UUID transactionId, Instant timestamp) {
        return new TransactionMessage(
                transactionId,
                t.accountNumber(),
                t.money().amount(),
                t.withdraw(),
                timestamp);
    }

}
