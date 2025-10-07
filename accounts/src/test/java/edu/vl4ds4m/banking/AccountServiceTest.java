package edu.vl4ds4m.banking;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.vl4ds4m.banking.accounts.model.Account;
import org.vl4ds4m.banking.accounts.model.Currency;
import org.vl4ds4m.banking.accounts.model.Money;
import org.vl4ds4m.banking.accounts.repository.AccountRepository;
import org.vl4ds4m.banking.accounts.repository.CustomerRepository;
import org.vl4ds4m.banking.accounts.repository.model.AccountPe;
import org.vl4ds4m.banking.accounts.repository.model.CustomerPe;
import org.vl4ds4m.banking.accounts.service.AccountService;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.Set;

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
        var accountRepository = mockAccountRepository();

        var createdAccount = new AccountPe();
        createdAccount.setNumber(123L);

        when(accountRepository.save(any()))
                .thenReturn(createdAccount);

        var service = new AccountService(accountRepository, mockCustomerRepository());

        // Act
        var account = service.createAccount(DEFAULT_CLIENT_NAME, Currency.CNY);

        // Assert
        assertNotNull(account.getNumber());
        assertEquals(Currency.CNY, account.getCurrency());
        assertEquals(Money.ZERO, account.getMoney());
    }

    @DisplayName("Ошибка при создании имеющегося счёта")
    @Test
    void testCreateDuplicateAccount() {
        // Arrange
        var currency = DEFAULT_ACCOUNT.getCurrency();
        var service = new AccountService(null, mockCustomerRepository());

        // Act & Assert
        var e = assertThrows(RuntimeException.class,
                () -> service.createAccount(DEFAULT_CLIENT_NAME, currency));
        assertEquals(
                "Account[customerName=" + DEFAULT_CLIENT_NAME +
                        ",currency=" + currency + "] already existed",
                e.getMessage());
    }

    @DisplayName("Ошибка при создании счёта для несуществующего клиента")
    @Test
    void testCreateAccountForAbsentCustomer() {
        // Arrange
        AccountService service = new AccountService(null, mockCustomerRepository());

        // Act & Assert
        var e = assertThrows(RuntimeException.class,
                () -> service.createAccount("unknown_client", Currency.EUR));
        assertEquals("Customer[name=unknown_client] not found", e.getMessage());
    }

    @DisplayName("Получение счёта по его номеру")
    @Test
    void testGetAccountByNumber() {
        // Arrange
        AccountService service = new AccountService(mockAccountRepository(), null);

        // Act
        Account account = service.getAccountByNumber(DEFAULT_ACCOUNT.getNumber());

        // Assert
        assertEquals(DEFAULT_ACCOUNT, account);
    }

    @DisplayName("Ошибка при запросе несуществующего счёта")
    @Test
    void testGetAbsentAccount() {
        // Arrange
        AccountService service = new AccountService(mockAccountRepository(), null);

        // Act & Assert
        var e = assertThrows(RuntimeException.class, () -> service.getAccountByNumber(935L));
        assertEquals("Account[number=935] not found", e.getMessage());
    }

    private static Account createDefaultAccount() {
        return new Account(
                9876543210L,
                Currency.RUB,
                new Money(new BigDecimal("7529.83")));
    }

    private static AccountRepository mockAccountRepository() {
        AccountRepository repository = mock();

        when(repository.findById(any()))
                .thenReturn(Optional.empty());

        var account = new AccountPe();
        account.setNumber(DEFAULT_ACCOUNT.getNumber());
        account.setCurrency(DEFAULT_ACCOUNT.getCurrency());
        account.setAmount(DEFAULT_ACCOUNT.getMoney().amount());

        when(repository.findById(DEFAULT_ACCOUNT.getNumber()))
                .thenReturn(Optional.of(account));

        return repository;
    }

    private static CustomerRepository mockCustomerRepository() {
        CustomerRepository repository = mock();

        when(repository.findByName(any()))
                .thenReturn(Optional.empty());

        var customer = new CustomerPe();
        customer.setName("my_client");

        var account = new AccountPe();
        account.setCustomer(customer);
        account.setNumber(DEFAULT_ACCOUNT.getNumber());
        account.setCurrency(DEFAULT_ACCOUNT.getCurrency());
        account.setAmount(DEFAULT_ACCOUNT.getMoney().amount());

        customer.setAccounts(Set.of(account));

        when(repository.findByName("my_client"))
                .thenReturn(Optional.of(customer));

        return repository;
    }
}
