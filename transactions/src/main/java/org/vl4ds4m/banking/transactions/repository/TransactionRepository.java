package org.vl4ds4m.banking.transactions.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.vl4ds4m.banking.transactions.repository.entity.TransactionRe;

public interface TransactionRepository extends MongoRepository<TransactionRe, String> {}
