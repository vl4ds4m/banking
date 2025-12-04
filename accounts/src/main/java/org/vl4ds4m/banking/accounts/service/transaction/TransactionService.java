package org.vl4ds4m.banking.accounts.service.transaction;

import org.vl4ds4m.banking.common.entity.Transaction;

public interface TransactionService {

    void sendTransactions(Transaction... transactions);

}
