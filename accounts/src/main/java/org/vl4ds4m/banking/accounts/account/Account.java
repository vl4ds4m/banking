package org.vl4ds4m.banking.accounts.account;

import jakarta.persistence.*;
import org.vl4ds4m.banking.common.Conversions;
import org.vl4ds4m.banking.accounts.customer.Customer;
import org.vl4ds4m.banking.accounts.transaction.Transaction;

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

    //@Enumerated(EnumType.STRING)
    @Column(name = CURRENCY, nullable = false)
    private String currency;

    @Column(name = "amount",
        nullable = false,
        precision = Conversions.PRECISION,
        scale = Conversions.SCALE)
    private BigDecimal amount;

    // @OneToMany(mappedBy = Transaction_.ACCOUNT)
    private List<Transaction> transactions;

    protected Account() {}

    public Account(Customer customer, String currency) {
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

    public String getCurrency() {
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
