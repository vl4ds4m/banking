package edu.vl4ds4m.tbank.dao;

import edu.vl4ds4m.tbank.dto.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TransactionRepository extends JpaRepository<Transaction, UUID> {
}
