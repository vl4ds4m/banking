package org.vl4ds4m.banking.accounts.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.vl4ds4m.banking.accounts.entity.Account;
import org.vl4ds4m.banking.accounts.entity.Customer;
import org.vl4ds4m.banking.accounts.repository.CustomerRepository;
import org.vl4ds4m.banking.accounts.repository.entity.CustomerRe;
import org.vl4ds4m.banking.common.entity.Money;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Repository
@RequiredArgsConstructor
public class CustomerDao {

    private final CustomerRepository repository;

    public Set<Customer> getAll() {
        var customers = repository.findAll();
        return StreamSupport.stream(customers.spliterator(), false)
                .map(c -> new Customer(
                        c.getLogin(),
                        c.getForename(),
                        c.getSurname(),
                        c.getBirthdate()))
                .collect(Collectors.toUnmodifiableSet());
    }

    public boolean existsByLogin(String login) {
        return repository.existsByLogin(login);
    }

    public Customer getByLogin(String login) {
        var re = getReByLogin(login);
        return new Customer(
                re.getLogin(),
                re.getForename(),
                re.getSurname(),
                re.getBirthdate());
    }

    public Set<Account> getAccounts(String login) {
        return getReByLogin(login)
                .getAccounts().stream()
                .map(re -> new Account(
                        re.getNumber(),
                        re.getCurrency(),
                        Money.of(re.getAmount())))
                .collect(Collectors.toSet());
    }

    public void create(Customer customer) {
        var re = new CustomerRe();
        re.setLogin(customer.login());
        re.setForename(customer.forename());
        re.setSurname(customer.surname());
        re.setBirthdate(customer.birthdate());
        repository.save(re);
    }

    private CustomerRe getReByLogin(String login) {
        return repository.findByLogin(login).orElseThrow();
    }
}
