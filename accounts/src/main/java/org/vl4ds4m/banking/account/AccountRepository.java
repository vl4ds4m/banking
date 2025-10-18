package org.vl4ds4m.banking.account;

import org.springframework.data.jpa.repository.JpaRepository;
import org.vl4ds4m.banking.currency.Currency;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Integer> {
    Optional<Account> findByCustomerIdAndCurrency(int customerId, Currency currency);
}
