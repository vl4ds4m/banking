package edu.tinkoff.model;

import edu.tinkoff.util.Conversions;
import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"customer_id", "currency"}))
public class Account {

    @Id
    @GeneratedValue
    private int number;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @Enumerated(EnumType.STRING)
    @Column(name = "currency")
    private Currency currency;

    private BigDecimal amount;

    public Account() {
        amount = Conversions.setScale(BigDecimal.ZERO);
    }

    public Account(Customer customer, Currency currency) {
        this();
        this.customer = customer;
        this.currency = currency;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
