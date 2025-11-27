package org.vl4ds4m.banking.accounts.repository.entity;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

import static org.vl4ds4m.banking.accounts.repository.entity.TransactionRe.ColumnNames.*;
import static org.vl4ds4m.banking.accounts.repository.entity.TransactionRe.TABLE_NAME;

@Entity
@Table(name = TABLE_NAME)
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class TransactionRe {

    static final String TABLE_NAME = "transactions";

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = ID)
    @EqualsAndHashCode.Include
    private UUID id;

    @Column(name = AMOUNT,
            nullable = false)
    private BigDecimal amount;

    @Column(name = WITHDRAW,
            nullable = false)
    private Boolean withdraw;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = ACCOUNT_ID,
            nullable = false)
    private AccountRe account;

    static class ColumnNames {

        static final String ID = "id";

        static final String AMOUNT = "amount";

        static final String WITHDRAW = "withdraw";

        static final String ACCOUNT_ID = "account_id";
    }
}
