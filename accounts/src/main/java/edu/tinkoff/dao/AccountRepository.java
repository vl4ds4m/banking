package edu.tinkoff.dao;

import edu.tinkoff.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Integer> {
    List<Account> findAllByCustomerId(int customerId);

    Optional<Account> findByCustomerIdAndCurrency(int customerId, String currency);
}
