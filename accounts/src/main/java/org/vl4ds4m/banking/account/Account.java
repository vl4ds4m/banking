package org.vl4ds4m.banking.account;

import jakarta.persistence.*;
import org.vl4ds4m.banking.Conversions;
import org.vl4ds4m.banking.currency.Currency;
import org.vl4ds4m.banking.customer.Customer;
import org.vl4ds4m.banking.transaction.Transaction;
import org.vl4ds4m.banking.transaction.Transaction_;

import java.math.BigDecimal;
import java.util.List;

// @Entity
@Table(name = "accounts",
    uniqueConstraints = @UniqueConstraint(
        columnNames = {Account.CUSTOMER_ID, Account.CURRENCY}))
public class Account {
    static final String CUSTOMER_ID = "customer_id";
    static final String CURRENCY = "currency";

    @Id
    @GeneratedValue
    @Column(name = "number")
    private Integer number;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = CUSTOMER_ID)
    private Customer customer;

    @Enumerated(EnumType.STRING)
    @Column(name = CURRENCY, nullable = false)
    private Currency currency;

    @Column(name = "amount",
        nullable = false,
        precision = Conversions.PRECISION,
        scale = Conversions.SCALE)
    private BigDecimal amount;

    @OneToMany(mappedBy = Transaction_.ACCOUNT)
    private List<Transaction> transactions;

    protected Account() {}

    public Account(Customer customer, Currency currency) {
        this.amount = BigDecimal.ZERO;
        this.customer = customer;
        this.currency = currency;
    }

    public int getNumber() {
        return number;
    }

    public Customer getCustomer() {
        return customer;
    }

    public Currency getCurrency() {
        return currency;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }
}
