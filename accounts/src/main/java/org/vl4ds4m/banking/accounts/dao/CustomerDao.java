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

@Repository
@RequiredArgsConstructor
public class CustomerDao {

    private final CustomerRepository repository;

    public boolean existsByName(String name) {
        return repository.existsByName(name);
    }

    public Customer getByName(String name) {
        var re = getReByName(name);
        return new Customer(
                re.getName(),
                re.getFirstName(),
                re.getLastName(),
                re.getBirthDate());
    }

    public Set<Account> getAccounts(String customerName) {
        return getReByName(customerName)
                .getAccounts().stream()
                .map(re -> new Account(
                        re.getNumber(),
                        re.getCurrency(),
                        Money.of(re.getAmount())))
                .collect(Collectors.toSet());
    }

    public void create(Customer customer) {
        var re = new CustomerRe();
        re.setName(customer.name());
        re.setFirstName(customer.firstName());
        re.setLastName(customer.lastName());
        re.setBirthDate(customer.birthDate());
        repository.save(re);
    }

    private CustomerRe getReByName(String name) {
        return repository.findByName(name).orElseThrow();
    }
}
