package edu.vl4ds4m.banking.transaction;

import io.micrometer.observation.annotation.Observed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class TransactionService {
    private static final Logger logger = LoggerFactory.getLogger(TransactionService.class);

    private final TransactionRepository repository;

    public TransactionService(TransactionRepository repository) {
        this.repository = repository;
    }

    @Observed
    public Transaction persist(Transaction transaction) {
        transaction = repository.save(transaction);
        logger.debug("Transaction[id={}] saved", transaction.getId());
        return transaction;
    }
}
