package org.vl4ds4m.banking.repository.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.vl4ds4m.banking.entity.Account;
import org.vl4ds4m.banking.entity.Currency;
import org.vl4ds4m.banking.entity.Money;

import java.math.BigDecimal;

@Entity
@Table(name = AccountRe.TABLE_NAME,
        uniqueConstraints = @UniqueConstraint(columnNames = {
                AccountRe.ColumnName.CUSTOMER_ID,
                AccountRe.ColumnName.CURRENCY}))
@Data
@NoArgsConstructor
public class AccountRe {

    static final String TABLE_NAME = "accounts";

    @Id
    @GeneratedValue
    @Column(name = ColumnName.ID)
    private Long id;

    @Column(name = ColumnName.NUMBER,
            nullable = false,
            unique = true)
    private Long number;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = ColumnName.CUSTOMER_ID,
            nullable = false)
    private CustomerRe customer;

    @Enumerated(EnumType.STRING)
    @Column(name = ColumnName.CURRENCY,
            nullable = false)
    private Currency currency;

    @Column(name = ColumnName.AMOUNT,
            nullable = false)
    private BigDecimal amount;

    public Account toEntity() {
        return new Account(number, currency, Money.of(amount));
    }

    static class ColumnName {

        static final String ID = "id";

        static final String NUMBER = "number";

        static final String CUSTOMER_ID = "customer_id";

        static final String CURRENCY = "currency";

        static final String AMOUNT = "amount";
    }
}
