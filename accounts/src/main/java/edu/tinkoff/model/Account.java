package edu.tinkoff.model;

import jakarta.persistence.*;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"customer_id", "currency"}))
public class Account {

    @Id
    @GeneratedValue
    private int number;

    @ManyToOne
    private Customer customer;

    private String currency;

    private double amount;

    public Account() {
        amount = 0.0;
    }

    public Account(Customer customer, String currency) {
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

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
