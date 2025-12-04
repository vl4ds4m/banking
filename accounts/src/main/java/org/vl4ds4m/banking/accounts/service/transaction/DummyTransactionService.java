package org.vl4ds4m.banking.accounts.service.transaction;

import org.vl4ds4m.banking.common.entity.Transaction;

public class DummyTransactionService implements TransactionService {

    @Override
    public void sendTransactions(Transaction... transactions) {}

}
