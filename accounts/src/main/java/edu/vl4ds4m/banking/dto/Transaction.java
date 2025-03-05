package edu.vl4ds4m.banking.dto;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Basic(optional = false)
    private Double amount;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Account account;

    public Transaction() {
    }

    public Transaction(Account account, Double amount) {
        this.account = account;
        this.amount = amount;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }
}
