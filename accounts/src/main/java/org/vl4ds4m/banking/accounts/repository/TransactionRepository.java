package org.vl4ds4m.banking.accounts.repository;

import org.springframework.data.repository.CrudRepository;
import org.vl4ds4m.banking.accounts.repository.entity.TransactionRe;

import java.util.UUID;

public interface TransactionRepository extends CrudRepository<TransactionRe, UUID> {}
