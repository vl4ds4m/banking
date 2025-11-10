package org.vl4ds4m.banking.accounts.deprecation.transaction;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.UUID;

@NoRepositoryBean
public interface TransactionRepository extends JpaRepository<Transaction, UUID> {
}
