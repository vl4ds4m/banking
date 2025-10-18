package org.vl4ds4m.banking.customer;

import jakarta.persistence.*;
import org.vl4ds4m.banking.account.Account;
import org.vl4ds4m.banking.account.Account_;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "customers")
public class Customer {
    @Id
    @GeneratedValue
    @Column(name = "id")
    private Integer id;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;

    @OneToMany(mappedBy = Account_.CUSTOMER)
    protected Set<Account> accounts;

    public Customer() {}

    public Customer(String firstName, String lastName, LocalDate birthDate) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Set<Account> getAccounts() {
        return accounts;
    }

    public void setAccounts(Set<Account> accounts) {
        this.accounts = accounts;
    }

    @Override
    public int hashCode() {
        return Objects.requireNonNullElse(this.id, 0);
    }

    @Override
    public boolean equals(Object obj) {
        boolean eq;
        if (obj == null) eq = false;
        else if (this == obj) eq = true;
        else eq = obj instanceof Customer o && Objects.equals(this.id, o.id);
        return eq;
    }
}
