package edu.vl4ds4m.banking.customer;

import edu.vl4ds4m.banking.account.Account;
import edu.vl4ds4m.banking.account.Account_;
import jakarta.persistence.*;

import java.time.LocalDate;
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

    protected Customer() {}

    public Customer(String firstName, String lastName, LocalDate birthDate) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;
    }

    public int getId() {
        return id;
    }

    public Set<Account> getAccounts() {
        return accounts;
    }
}
