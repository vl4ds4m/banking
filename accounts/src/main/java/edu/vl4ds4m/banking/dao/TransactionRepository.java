package edu.vl4ds4m.banking.dao;

import edu.vl4ds4m.banking.dto.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TransactionRepository extends JpaRepository<Transaction, UUID> {
}
