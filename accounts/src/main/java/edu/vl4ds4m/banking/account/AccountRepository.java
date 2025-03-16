package edu.vl4ds4m.banking.account;

import edu.vl4ds4m.banking.currency.Currency;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Integer> {
    Optional<Account> findByCustomerIdAndCurrency(int customerId, Currency currency);
}
