package edu.vl4ds4m.tbank.dao;

import edu.vl4ds4m.tbank.dto.Account;
import edu.vl4ds4m.tbank.dto.Currency;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Integer> {
    Optional<Account> findByCustomerIdAndCurrency(int customerId, Currency currency);
}
