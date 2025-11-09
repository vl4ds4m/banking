package org.vl4ds4m.banking.accounts.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.vl4ds4m.banking.accounts.entity.Account;
import org.vl4ds4m.banking.accounts.repository.AccountRepository;
import org.vl4ds4m.banking.accounts.repository.CustomerRepository;
import org.vl4ds4m.banking.accounts.repository.entity.AccountRe;
import org.vl4ds4m.banking.common.entity.Currency;
import org.vl4ds4m.banking.common.entity.Money;

@Repository
@RequiredArgsConstructor
public class AccountDao {

    private final AccountRepository accountRepository;

    private final CustomerRepository customerRepository;

    public boolean existsByNumber(long number) {
        return accountRepository.existsByNumber(number);
    }

    public Account getByNumber(long number) {
        var re = getReByNumber(number);
        return new Account(
                re.getNumber(),
                re.getCurrency(),
                Money.of(re.getAmount()));
    }

    public long create(String customerName, Currency currency, Money money) {
        var accountRe = new AccountRe();
        accountRe.setNumber(0L);
        accountRe.setCurrency(currency);
        accountRe.setAmount(money.amount());

        var customerRe = customerRepository.findByNickname(customerName).orElseThrow();
        accountRe.setCustomer(customerRe);

        accountRe = accountRepository.save(accountRe);

        long number = generateNumber(accountRe.getId());
        accountRe.setNumber(number);

        accountRepository.save(accountRe);

        return number;
    }

    public void updateMoney(long accountNumber, Money money) {
        var re = getReByNumber(accountNumber);
        re.setAmount(money.amount());
        accountRepository.save(re);
    }

    private AccountRe getReByNumber(long number) {
        return accountRepository.findByNumber(number).orElseThrow();
    }

    // ToDo implement proper generator
    private static long generateNumber(long id) {
        long number = id + 1_000_000_000L;
        if (number <= 0L) {
            throw new RuntimeException("New account number must be positive. " +
                    "Generated value = " + number);
        }
        return number;
    }
}
