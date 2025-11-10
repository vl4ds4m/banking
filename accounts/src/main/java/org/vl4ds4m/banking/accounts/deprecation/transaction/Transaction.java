package org.vl4ds4m.banking.accounts.deprecation.transaction;

import jakarta.persistence.*;
import org.vl4ds4m.banking.accounts.entity.Account;
import org.vl4ds4m.banking.common.Conversions;

import java.math.BigDecimal;
import java.util.UUID;

// @Entity
@Table(name = "transactions")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @Column(name = "amount",
        nullable = false,
        precision = Conversions.PRECISION,
        scale = Conversions.SCALE)
    private BigDecimal amount;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "account_id")
    private Account account;

    protected Transaction() {}

    public Transaction(Account account, BigDecimal amount) {
        this.account = account;
        this.amount = amount;
    }

    public UUID getId() {
        return id;
    }

    public BigDecimal getAmount() {
        return amount;
    }
}
