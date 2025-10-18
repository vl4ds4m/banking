package org.vl4ds4m.banking.service;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.lang.NonNull;
import org.vl4ds4m.banking.entity.Account;
import org.vl4ds4m.banking.entity.Currency;
import org.vl4ds4m.banking.entity.Money;
import org.vl4ds4m.banking.repository.AccountRepository;
import org.vl4ds4m.banking.repository.CustomerRepository;
import org.vl4ds4m.banking.repository.entity.AccountRe;
import org.vl4ds4m.banking.repository.entity.CustomerRe;
import org.vl4ds4m.banking.util.TestRepository;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


class AccountServiceTest {

    private static final Account DEFAULT_ACCOUNT = createDefaultAccount();

    private static final String DEFAULT_CLIENT_NAME = "my_client";

    @DisplayName("Создание счёта")
    @Test
    void testCreateAccount() {
        // Arrange
        var createdAccount = new AccountRe();
        createdAccount.setNumber(123L);

        var service = new AccountService(fakeAccountRepository(), mockCustomerRepository());

        // Act
        var account = service.createAccount(DEFAULT_CLIENT_NAME, Currency.CNY);

        // Assert
        assertNotNull(account.getNumber());
        assertEquals(Currency.CNY, account.getCurrency());
        assertEquals(Money.ZERO, account.getMoney());
    }

    @DisplayName("Ошибка при создании имеющегося счёта")
    @Test
    void testCreateDuplicateAccountFailed() {
        // Arrange
        var currency = DEFAULT_ACCOUNT.getCurrency();
        var service = new AccountService(null, mockCustomerRepository());

        // Act & Assert
        var e = assertThrows(ServiceException.class,
                () -> service.createAccount(DEFAULT_CLIENT_NAME, currency));
        assertEquals(
                "Account[customerName=" + DEFAULT_CLIENT_NAME +
                        ",currency=" + currency + "] already existed",
                e.getMessage());
    }

    @DisplayName("Ошибка при создании счёта для несуществующего клиента")
    @Test
    void testCreateAccountForAbsentCustomerFailed() {
        // Arrange
        AccountService service = new AccountService(null, mockCustomerRepository());

        // Act & Assert
        var e = assertThrows(ServiceException.class,
                () -> service.createAccount("unknown_client", Currency.EUR));
        assertEquals("Customer[name=unknown_client] not found", e.getMessage());
    }

    @DisplayName("Получение счёта по его номеру")
    @Test
    void testGetAccountByNumber() {
        // Arrange
        AccountService service = new AccountService(fakeAccountRepository(), null);

        // Act
        Account account = service.getAccountByNumber(DEFAULT_ACCOUNT.getNumber());

        // Assert
        assertEquals(DEFAULT_ACCOUNT, account);
    }

    @DisplayName("Ошибка при запросе несуществующего счёта")
    @Test
    void testGetAbsentAccount() {
        // Arrange
        AccountService service = new AccountService(fakeAccountRepository(), null);

        // Act & Assert
        var e = assertThrows(ServiceException.class, () -> service.getAccountByNumber(935L));
        assertEquals("Account[number=935] not found", e.getMessage());
    }

    @DisplayName("Пополнение счёта")
    @Test
    void testTopUpAccount() {
        // Arrange
        var number = DEFAULT_ACCOUNT.getNumber();
        var money = new Money(BigDecimal.TWO);
        var service = new AccountService(fakeAccountRepository(), null);

        // Act
        service.topUpAccount(number, money.amount());
        var actual = service.getAccountByNumber(number);

        // Assert
        var expectedMoney = DEFAULT_ACCOUNT.getMoney().add(money);
        var expected = new Account(number, DEFAULT_ACCOUNT.getCurrency(), expectedMoney);
        assertEquals(expected, actual);
    }

    @DisplayName("Снятие денег со счёта")
    @Test
    void testWithdrawMoneyToAccount() {
        // Arrange
        var number = DEFAULT_ACCOUNT.getNumber();
        var money = new Money(new BigDecimal("3.67"));
        var service = new AccountService(fakeAccountRepository(), null);

        // Act
        service.withdrawMoneyToAccount(number, money.amount());
        var account = service.getAccountByNumber(number);

        // Assert
        var expectedMoney = DEFAULT_ACCOUNT.getMoney().subtract(money);
        var expected = new Account(number, DEFAULT_ACCOUNT.getCurrency(), expectedMoney);
        assertEquals(expected, account);
    }

    @DisplayName("Перевод денег с одного счёта на другой в одной валюте")
    @Test
    void testTransferMoneyForEqualCurrencies() {
        // Arrange
        var currency = DEFAULT_ACCOUNT.getCurrency();
        var money = new Money(new BigDecimal("276.13"));
        var senderNumber = DEFAULT_ACCOUNT.getNumber();
        var accountRepository = fakeAccountRepository();
        var receiverMoney = new Money(new BigDecimal("751.02"));
        var receiverNumber = accountRepository.save(
                new AccountRe(null, currency, receiverMoney.amount())
        ).getNumber();
        var service = new AccountService(accountRepository, null);

        // Act
        service.transferMoney(senderNumber, receiverNumber, money.amount());
        var sender = service.getAccountByNumber(senderNumber);
        var receiver = service.getAccountByNumber(receiverNumber);

        // Arrange
        var expectedSender = new Account(senderNumber, currency,
                DEFAULT_ACCOUNT.getMoney().subtract(money));
        var expectedReceiver = new Account(receiverNumber, currency,
                receiverMoney.add(money));
        assertEquals(expectedSender, sender);
        assertEquals(expectedReceiver, receiver);
    }

    @DisplayName("Перевод денег с одного счёта на другой в разных валютах")
    @Test
    @Disabled("Необходимо реализовать ConverterService")
    void testTransferMoneyForDifferentCurrencies() {}

    private static Account createDefaultAccount() {
        return new Account(
                9876543210L,
                Currency.RUB,
                new Money(new BigDecimal("7529.83")));
    }

    private static AccountRepository fakeAccountRepository() {
        AccountRepository repository = new AccountTestRepository();

        var account = new AccountRe();
        account.setNumber(DEFAULT_ACCOUNT.getNumber());
        account.setCurrency(DEFAULT_ACCOUNT.getCurrency());
        account.setAmount(DEFAULT_ACCOUNT.getMoney().amount());

        repository.save(account);

        return repository;
    }

    private static CustomerRepository mockCustomerRepository() {
        CustomerRepository repository = mock();

        when(repository.findByName(any()))
                .thenReturn(Optional.empty());

        var customer = new CustomerRe();
        customer.setName("my_client");

        var account = new AccountRe();
        account.setCustomer(customer);
        account.setNumber(DEFAULT_ACCOUNT.getNumber());
        account.setCurrency(DEFAULT_ACCOUNT.getCurrency());
        account.setAmount(DEFAULT_ACCOUNT.getMoney().amount());

        customer.setAccounts(Set.of(account));

        when(repository.findByName("my_client"))
                .thenReturn(Optional.of(customer));

        return repository;
    }

    private static class AccountTestRepository
            extends TestRepository<AccountRe, Long>
            implements AccountRepository
    {
        private final AtomicLong nextId = new AtomicLong();

        @Override
        @NonNull
        protected Optional<Long> extractId(@NonNull AccountRe entity) {
            return Optional.ofNullable(entity.getNumber());
        }

        @Override
        protected void setId(@NonNull Long id, @NonNull AccountRe entity) {
            entity.setNumber(id);
        }

        @Override
        @NonNull
        protected Long produceNextId() {
            return nextId.incrementAndGet();
        }
    }
}
