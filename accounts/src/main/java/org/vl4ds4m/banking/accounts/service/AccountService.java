package org.vl4ds4m.banking.accounts.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.vl4ds4m.banking.accounts.dao.AccountDao;
import org.vl4ds4m.banking.accounts.dao.CustomerDao;
import org.vl4ds4m.banking.accounts.entity.Account;
import org.vl4ds4m.banking.accounts.entity.Customer;
import org.vl4ds4m.banking.accounts.service.expection.ServiceException;
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

    public Account getAccount(long accountNumber) {
        checkAccountExists(accountNumber);
        return accountDao.getByNumber(accountNumber);
    }

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

    @Transactional(isolation = Isolation.REPEATABLE_READ)
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

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public Account withdrawMoneyToAccount(long accountNumber, Money subtrahend) {
        checkAccountExists(accountNumber);

        var account = accountDao.getByNumber(accountNumber);

        if (account.money().compareTo(subtrahend) < 0) {
            throw new ServiceException("Account money is less then subtrahend");
        }

        var money = account.money().subtract(subtrahend);

        accountDao.updateMoney(accountNumber, money);
        log.info("{} withdraw {} {}",
                Account.logStr(accountNumber),
                subtrahend,
                account.currency());

        return new Account(accountNumber, account.currency(), money);
    }

    private void checkAccountExists(long accountNumber) {
        if (!accountDao.existsByNumber(accountNumber)) {
            throw new EntityNotFoundException(Account.logStr(accountNumber));
        }
    }
}
