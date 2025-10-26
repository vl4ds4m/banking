package org.vl4ds4m.banking.accounts.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.vl4ds4m.banking.accounts.dao.AccountDao;
import org.vl4ds4m.banking.accounts.dao.CustomerDao;
import org.vl4ds4m.banking.accounts.entity.Account;
import org.vl4ds4m.banking.accounts.entity.Customer;
import org.vl4ds4m.banking.common.entity.Currency;
import org.vl4ds4m.banking.common.entity.Money;
import org.vl4ds4m.banking.accounts.service.expection.DuplicateEntityException;
import org.vl4ds4m.banking.accounts.service.expection.EntityNotFoundException;

@Service
@Slf4j
@RequiredArgsConstructor
public class AccountService {

    private final AccountDao accountDao;

    private final CustomerDao customerDao;

    public long createAccount(String customerName, Currency currency) {
        if (!customerDao.existsByName(customerName)) {
            throw new EntityNotFoundException(Customer.logStr(customerName));
        }

        boolean exists = customerDao.getAccounts(customerName).stream()
                .map(Account::currency)
                .anyMatch(currency::equals);

        if (exists) {
            throw new DuplicateEntityException(Account.logStr(customerName, currency));
        }

        long number = accountDao.create(customerName, currency, Money.empty());
        log.info("{} created", Account.logStr(number));

        return number;
    }

    public Account getAccount(long accountNumber) {
        checkAccountExists(accountNumber);
        return accountDao.getByNumber(accountNumber);
    }

    public Account topUpAccount(long accountNumber, Money augend) {
        checkAccountExists(accountNumber);

        var account = accountDao.getByNumber(accountNumber);
        var money = account.money().add(augend);

        accountDao.updateMoney(accountNumber, money);
        log.info("{} top up by {} {}",
                Account.logStr(accountNumber),
                augend,
                account.currency());

        return new Account(accountNumber, account.currency(), money);
    }

    public Account withdrawMoneyToAccount(long accountNumber, Money subtrahend) {
        checkAccountExists(accountNumber);

        var account = accountDao.getByNumber(accountNumber);
        var money = account.money().subtract(subtrahend);

        accountDao.updateMoney(accountNumber, money);
        log.info("{} withdraw {} {}",
                Account.logStr(accountNumber),
                subtrahend,
                account.currency());

        return new Account(accountNumber, account.currency(), money);
    }

    // ToDo implement proper transfer
    public void transferMoney(long senderNumber, long receiverNumber, Money money) {
        checkAccountExists(senderNumber);
        checkAccountExists(receiverNumber);

        var sender = accountDao.getByNumber(senderNumber);
        var receiver = accountDao.getByNumber(receiverNumber);

        final var senderMoneyBefore = sender.money();
        final var senderMoneyAfter = senderMoneyBefore.subtract(money);
        accountDao.updateMoney(senderNumber, senderMoneyAfter);

        final var receiverMoneyBefore = receiver.money();
        final var receiverMoneyAfter = receiverMoneyBefore.add(money);
        accountDao.updateMoney(receiverNumber, receiverMoneyAfter);
    }

    private void checkAccountExists(long accountNumber) {
        if (!accountDao.existsByNumber(accountNumber)) {
            throw new EntityNotFoundException(Account.logStr(accountNumber));
        }
    }
}
