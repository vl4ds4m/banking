package org.vl4ds4m.banking.accounts.service;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.lang.NonNull;
import org.vl4ds4m.banking.accounts.api.model.CreateAccountRequest;
import org.vl4ds4m.banking.accounts.api.model.TopUpAccountRequest;
import org.vl4ds4m.banking.accounts.api.model.WithdrawAccountRequest;
import org.vl4ds4m.banking.accounts.api.util.CurrencyConverter;
import org.vl4ds4m.banking.accounts.entity.Account;
import org.vl4ds4m.banking.common.entity.Currency;
import org.vl4ds4m.banking.accounts.entity.Customer;
import org.vl4ds4m.banking.common.entity.Money;
import org.vl4ds4m.banking.accounts.repository.AccountRepository;
import org.vl4ds4m.banking.accounts.repository.CustomerRepository;
import org.vl4ds4m.banking.accounts.repository.entity.AccountRe;
import org.vl4ds4m.banking.accounts.repository.entity.CustomerRe;
import org.vl4ds4m.banking.accounts.service.expection.DuplicateEntityException;
import org.vl4ds4m.banking.accounts.service.expection.EntityNotFoundException;
import org.vl4ds4m.banking.accounts.util.TestEntity;
import org.vl4ds4m.banking.accounts.util.TestRepository;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AccountServiceTest {

    private static final Account DEFAULT_ACCOUNT = TestEntity.createDefaultAccount();

    private static final Customer DEFAULT_CUSTOMER = TestEntity.createDefaultCustomer();

    @DisplayName("Получение счёта по его номеру")
    @Test
    void testGetAccountByNumber() {
        // Arrange
        var service = new AccountService(fakeAccountRepository(), null);

        // Act
        var account = service.getAccountByNumber(DEFAULT_ACCOUNT.number());

        // Assert
        assertEquals(DEFAULT_ACCOUNT, account);
    }

    @DisplayName("Ошибка при запросе несуществующего счёта")
    @Test
    void testGetAbsentAccountFailed() {
        // Arrange
        AccountService service = new AccountService(fakeAccountRepository(), null);

        // Act & Assert
        var e = assertThrows(EntityNotFoundException.class, () -> service.getAccountByNumber(935L));
        assertEquals("Account[number=935] not found", e.getMessage());
    }

    @DisplayName("Создание счёта")
    @Test
    void testCreateAccount() {
        // Arrange
        var service = new AccountService(fakeAccountRepository(), mockCustomerRepository());
        var request = new CreateAccountRequest(
                DEFAULT_CUSTOMER.name(),
                CurrencyConverter.toApi(Currency.CNY));

        // Act
        var response = service.createAccount(request);

        // Assert
        assertTrue(response.getNumber() > 0L);

        var account = service.getAccountByNumber(response.getNumber());
        assertEquals(response.getNumber(), account.number());
        assertEquals(Currency.CNY, account.currency());
        assertEquals(Money.empty(), account.money());
    }

    @DisplayName("Ошибка при создании имеющегося счёта")
    @Test
    void testCreateDuplicateAccountFailed() {
        // Arrange
        var service = new AccountService(null, mockCustomerRepository());
        var request = new CreateAccountRequest(
                DEFAULT_CUSTOMER.name(),
                CurrencyConverter.toApi(DEFAULT_ACCOUNT.currency()));

        // Act & Assert
        var e = assertThrows(DuplicateEntityException.class, () -> service.createAccount(request));
        assertEquals(
                "Account[customerName=" + DEFAULT_CUSTOMER.name() +
                        ",currency=" + DEFAULT_ACCOUNT.currency() +
                        "] already exists",
                e.getMessage());
    }

    @DisplayName("Ошибка при создании счёта для несуществующего клиента")
    @Test
    void testCreateAccountForAbsentCustomerFailed() {
        // Arrange
        var service = new AccountService(null, mockCustomerRepository());
        var request = new CreateAccountRequest("unknown_client", CurrencyConverter.toApi(Currency.EUR));

        // Act & Assert
        var e = assertThrows(EntityNotFoundException.class, () -> service.createAccount(request));
        assertEquals("Customer[name=unknown_client] not found", e.getMessage());
    }

    @DisplayName("Получение баланса по счёту")
    @Test
    void testGetAccountBalance() {
        // Arrange
        var service = new AccountService(fakeAccountRepository(), null);

        // Act
        var response = service.getAccountBalance(DEFAULT_ACCOUNT.number());

        // Assert
        assertEquals(CurrencyConverter.toApi(DEFAULT_ACCOUNT.currency()), response.getCurrency());
        assertEquals(DEFAULT_ACCOUNT.money().amount(), response.getAmount());
    }

    @DisplayName("Пополнение счёта")
    @Test
    void testTopUpAccount() {
        // Arrange
        var service = new AccountService(fakeAccountRepository(), null);
        var number = DEFAULT_ACCOUNT.number();
        var money = Money.of(BigDecimal.TWO);
        var request = new TopUpAccountRequest(money.amount());

        // Act
        var response = service.topUpAccount(number, request);

        // Assert
        var expectedCurrency = CurrencyConverter.toApi(DEFAULT_ACCOUNT.currency());
        var expectedAmount = DEFAULT_ACCOUNT.money().add(money).amount();
        assertEquals(expectedCurrency, response.getCurrency());
        assertEquals(expectedAmount, response.getTotalAmount());
    }

    @DisplayName("Снятие денег со счёта")
    @Test
    void testWithdrawMoneyToAccount() {
        // Arrange
        var service = new AccountService(fakeAccountRepository(), null);
        var number = DEFAULT_ACCOUNT.number();
        var money = Money.of(new BigDecimal("3.67"));
        var request = new WithdrawAccountRequest(money.amount());

        // Act
        var response = service.withdrawMoneyToAccount(number, request);

        // Assert
        var expectedCurrency = CurrencyConverter.toApi(DEFAULT_ACCOUNT.currency());
        var expectedAmount = DEFAULT_ACCOUNT.money().subtract(money).amount();
        assertEquals(expectedCurrency, response.getCurrency());
        assertEquals(expectedAmount, response.getTotalAmount());
    }

    @DisplayName("Перевод денег с одного счёта на другой в одной валюте")
    @Test
    void testTransferMoneyForEqualCurrencies() {
        // Arrange
        var currency = DEFAULT_ACCOUNT.currency();

        long senderNumber = DEFAULT_ACCOUNT.number();

        long receiverNumber = 237371236L;
        var receiverAccount = new AccountRe();
        var receiverMoney = Money.of(new BigDecimal("751.02"));
        receiverAccount.setNumber(receiverNumber);
        receiverAccount.setCurrency(currency);
        receiverAccount.setAmount(receiverMoney.amount());

        var accountRepository = fakeAccountRepository();
        accountRepository.save(receiverAccount);

        var money = Money.of(new BigDecimal("276.13"));

        var service = new AccountService(accountRepository, null);

        // Act
        service.transferMoney(senderNumber, receiverNumber, money.amount());

        // Arrange
        var expectedSender = new Account(senderNumber, currency,
                DEFAULT_ACCOUNT.money().subtract(money));
        var expectedReceiver = new Account(receiverNumber, currency,
                receiverMoney.add(money));

        var actualSender = service.getAccountByNumber(senderNumber);
        var actualReceiver = service.getAccountByNumber(receiverNumber);

        assertEquals(expectedSender, actualSender);
        assertEquals(expectedReceiver, actualReceiver);
    }

    @DisplayName("Перевод денег с одного счёта на другой в разных валютах")
    @Test
    @Disabled("Необходимо реализовать ConverterService")
    void testTransferMoneyForDifferentCurrencies() {}

    private static AccountRepository fakeAccountRepository() {
        AccountRepository repository = new AccountTestRepository();

        var account = new AccountRe();
        account.setNumber(DEFAULT_ACCOUNT.number());
        account.setCurrency(DEFAULT_ACCOUNT.currency());
        account.setAmount(DEFAULT_ACCOUNT.money().amount());

        repository.save(account);

        return repository;
    }

    private static CustomerRepository mockCustomerRepository() {
        CustomerRepository repository = mock();

        when(repository.findByName(any())).thenReturn(Optional.empty());

        var customer = new CustomerRe();
        customer.setName(DEFAULT_CUSTOMER.name());

        var account = new AccountRe();
        account.setCustomer(customer);
        account.setNumber(DEFAULT_ACCOUNT.number());
        account.setCurrency(DEFAULT_ACCOUNT.currency());
        account.setAmount(DEFAULT_ACCOUNT.money().amount());

        customer.setAccounts(Set.of(account));

        when(repository.findByName(DEFAULT_CUSTOMER.name()))
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
            return Optional.ofNullable(entity.getId());
        }

        @Override
        protected void setId(@NonNull Long id, @NonNull AccountRe entity) {
            entity.setId(id);
        }

        @Override
        @NonNull
        protected Long produceNextId() {
            return nextId.incrementAndGet();
        }

        @Override
        public Optional<AccountRe> findByNumber(Long number) {
            for (var account : getAll()) {
                if (account.getNumber().equals(number)) {
                    return Optional.of(extract(account.getId()));
                }
            }
            return Optional.empty();
        }
    }
}
