package org.vl4ds4m.banking.accounts.account;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.Optional;

@NoRepositoryBean
public interface AccountRepository extends JpaRepository<Account, Integer> {
    Optional<Account> findByCustomerIdAndCurrency(int customerId, String currency);
}
