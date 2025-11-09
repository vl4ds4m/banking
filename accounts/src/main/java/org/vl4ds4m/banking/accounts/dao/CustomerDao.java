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

    public boolean existsByNickname(String nickname) {
        return repository.existsByNickname(nickname);
    }

    public Customer getByNickname(String nickname) {
        var re = getReByNickname(nickname);
        return new Customer(
                re.getNickname(),
                re.getForename(),
                re.getSurname(),
                re.getBirthdate());
    }

    public Set<Account> getAccounts(String nickname) {
        return getReByNickname(nickname)
                .getAccounts().stream()
                .map(re -> new Account(
                        re.getNumber(),
                        re.getCurrency(),
                        Money.of(re.getAmount())))
                .collect(Collectors.toSet());
    }

    public void create(Customer customer) {
        var re = new CustomerRe();
        re.setNickname(customer.nickname());
        re.setForename(customer.forename());
        re.setSurname(customer.surname());
        re.setBirthdate(customer.birthdate());
        repository.save(re);
    }

    private CustomerRe getReByNickname(String nickname) {
        return repository.findByNickname(nickname).orElseThrow();
    }
}
