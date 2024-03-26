package edu.tinkoff.dao;

import edu.tinkoff.dto.Account;
import edu.tinkoff.dto.Currency;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Integer> {
    Optional<Account> findByCustomerIdAndCurrency(int customerId, Currency currency);
}
