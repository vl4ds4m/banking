package org.vl4ds4m.banking.accounts.dao;

import io.micrometer.observation.annotation.Observed;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.vl4ds4m.banking.accounts.repository.AccountRepository;
import org.vl4ds4m.banking.accounts.repository.TransactionRepository;
import org.vl4ds4m.banking.accounts.repository.entity.TransactionRe;
import org.vl4ds4m.banking.common.entity.Money;

@Repository
@RequiredArgsConstructor
public class TransactionDao {

    private final TransactionRepository transactionRepository;

    private final AccountRepository accountRepository;

    @Observed
    public void create(long accountNumber, Money money, boolean withdraw) {
        var transaction = new TransactionRe();
        transaction.setAmount(money.amount());
        transaction.setWithdraw(withdraw);

        var account = accountRepository.findByNumber(accountNumber).orElseThrow();
        transaction.setAccount(account);

        transactionRepository.save(transaction);
    }
}
