package org.vl4ds4m.banking.accounts.repository.entity;

import jakarta.persistence.*;
import lombok.*;
import org.vl4ds4m.banking.common.entity.Currency;

import java.math.BigDecimal;

import static org.vl4ds4m.banking.accounts.repository.entity.AccountRe.ColumnName.*;
import static org.vl4ds4m.banking.accounts.repository.entity.AccountRe.TABLE_NAME;

@Entity
@Table(name = TABLE_NAME,
    uniqueConstraints = {
        @UniqueConstraint(columnNames = AccountRe.ColumnName.NUMBER,
            name = TABLE_NAME + "_natkey"),
        @UniqueConstraint(columnNames = {CUSTOMER_ID, CURRENCY},
            name = TABLE_NAME + "_uniqkey" + "-" + CUSTOMER_ID + "-" + CURRENCY)})
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class AccountRe {

    static final String TABLE_NAME = "accounts";

    @Id
    @GeneratedValue
    @Column(name = ID)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(name = NUMBER,
            nullable = false)
    private Long number;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = CUSTOMER_ID,
            nullable = false)
    private CustomerRe customer;

    @Enumerated(EnumType.STRING)
    @Column(name = CURRENCY,
            nullable = false)
    private Currency currency;

    @Column(name = AMOUNT,
            nullable = false)
    private BigDecimal amount;

    static class ColumnName {

        static final String ID = "id";

        static final String NUMBER = "number";

        static final String CUSTOMER_ID = "customer_id";

        static final String CURRENCY = "currency";

        static final String AMOUNT = "amount";
    }
}
