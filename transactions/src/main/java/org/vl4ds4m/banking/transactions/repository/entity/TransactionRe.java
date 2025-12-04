package org.vl4ds4m.banking.transactions.repository.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Document("transactions")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class TransactionRe {

    @Id
    @EqualsAndHashCode.Include
    private String id;

    private UUID transactionId;

    private Long accountNumber;

    private BigDecimal amount;

    private Boolean withdraw;

    private Instant timestamp;

    public TransactionRe(
            UUID transactionId,
            long accountNumber,
            BigDecimal amount,
            boolean withdraw,
            Instant timestamp
    ) {
        this.transactionId = transactionId;
        this.accountNumber = accountNumber;
        this.amount = amount;
        this.withdraw = withdraw;
        this.timestamp = timestamp;
    }

}
