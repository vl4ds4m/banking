package edu.tinkoff.dao;

import edu.tinkoff.model.Account;
import edu.tinkoff.model.Currency;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Integer> {
    Optional<Account> findByCustomerIdAndCurrency(int customerId, Currency currency);
}
