package org.vl4ds4m.banking.accounts.repository.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.vl4ds4m.banking.accounts.model.Currency;

import java.math.BigDecimal;

@Entity
@Table(name = AccountPe.TABLE_NAME,
        uniqueConstraints = @UniqueConstraint(columnNames = {
                AccountPe.ColumnName.CUSTOMER_ID,
                AccountPe.ColumnName.CURRENCY}))
@Data
@NoArgsConstructor
public class AccountPe {

    static final String TABLE_NAME = "accounts";

    @Id
    @GeneratedValue
    @Column(name = ColumnName.NUMBER)
    private Long number;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = ColumnName.CUSTOMER_ID,
            nullable = false)
    private CustomerPe customer;

    @Enumerated(EnumType.STRING)
    @Column(name = ColumnName.CURRENCY,
            nullable = false)
    private Currency currency;

    @Column(name = ColumnName.AMOUNT,
            nullable = false)
    private BigDecimal amount;

    public AccountPe(CustomerPe customer, Currency currency, BigDecimal amount) {
        this.customer = customer;
        this.currency = currency;
        this.amount = amount;
    }

    static class ColumnName {

        static final String NUMBER = "number";

        static final String CUSTOMER_ID = "customer_id";

        static final String CURRENCY = "currency";

        static final String AMOUNT = "amount";
    }
}
