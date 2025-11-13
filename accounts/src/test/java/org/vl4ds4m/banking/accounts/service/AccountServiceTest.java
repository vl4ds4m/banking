package org.vl4ds4m.banking.accounts.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.vl4ds4m.banking.accounts.dao.AccountDao;
import org.vl4ds4m.banking.accounts.dao.CustomerDao;
import org.vl4ds4m.banking.accounts.entity.Account;
import org.vl4ds4m.banking.accounts.entity.Customer;
import org.vl4ds4m.banking.accounts.service.expection.DuplicateEntityException;
import org.vl4ds4m.banking.accounts.service.expection.EntityNotFoundException;
import org.vl4ds4m.banking.accounts.util.TestEntity;
import org.vl4ds4m.banking.common.entity.Currency;
import org.vl4ds4m.banking.common.entity.Money;
import org.vl4ds4m.banking.common.exception.InvalidQueryException;

import java.math.BigDecimal;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AccountServiceTest {

    private static final Account DEFAULT_ACCOUNT = TestEntity.createDefaultAccount();

    private static final Customer DEFAULT_CUSTOMER = TestEntity.createDefaultCustomer();

    @DisplayName("Получение счёта по его номеру")
    @Test
    void testGetAccountByNumber() {
        // Arrange
        var service = new AccountService(mockAccountDao(), mock());

        // Act
        var account = service.getAccount(DEFAULT_ACCOUNT.number());

        // Assert
        assertEquals(DEFAULT_ACCOUNT, account);
    }

    @DisplayName("Ошибка при запросе несуществующего счёта")
    @Test
    void testGetAbsentAccountFailed() {
        // Arrange
        var accountNumber = 935L;
        AccountService service = new AccountService(mockAccountDao(), mock());

        // Act & Assert
        var e = assertThrows(EntityNotFoundException.class, () -> service.getAccount(accountNumber));
        assertTrue(e.getMessage().contains("" + accountNumber));
    }

    @DisplayName("Создание счёта")
    @Test
    void testCreateAccount() {
        // Arrange
        var accountDao = mockAccountDao();
        when(accountDao.create(DEFAULT_CUSTOMER.nickname(), Currency.CNY, Money.empty()))
                .thenReturn(8346L);
        var service = new AccountService(accountDao, mockCustomerDao());

        // Act
        var accountNumber = service.createAccount(DEFAULT_CUSTOMER.nickname(), Currency.CNY);

        // Assert
        assertEquals(8346L, accountNumber);
    }

    @DisplayName("Ошибка при создании имеющегося счёта")
    @Test
    void testCreateDuplicateAccountFailed() {
        // Arrange
        var customerDao = mockCustomerDao();
        when(customerDao.getAccounts(DEFAULT_CUSTOMER.nickname()))
                .thenReturn(Set.of(DEFAULT_ACCOUNT));
        var service = new AccountService(mockAccountDao(), customerDao);

        // Act & Assert
        var e = assertThrows(DuplicateEntityException.class,
                () -> service.createAccount(DEFAULT_CUSTOMER.nickname(), DEFAULT_ACCOUNT.currency()));
        assertTrue(e.getMessage().contains(DEFAULT_CUSTOMER.nickname()));
        assertTrue(e.getMessage().contains("" + DEFAULT_ACCOUNT.currency()));
    }

    @DisplayName("Ошибка при создании счёта для несуществующего клиента")
    @Test
    void testCreateAccountForAbsentCustomerFailed() {
        // Arrange
        var customerNickname = "unknown_client";
        var service = new AccountService(mockAccountDao(), mockCustomerDao());

        // Act & Assert
        var e = assertThrows(EntityNotFoundException.class,
                () -> service.createAccount(customerNickname, Currency.EUR));
        assertTrue(e.getMessage().contains(customerNickname));
    }

    @DisplayName("Получение баланса по счёту")
    @Test
    void testGetAccountBalance() {
        // Arrange
        var service = new AccountService(mockAccountDao(), mock());

        // Act
        var response = service.getAccount(DEFAULT_ACCOUNT.number());

        // Assert
        assertEquals(DEFAULT_ACCOUNT.currency(), response.currency());
        assertEquals(DEFAULT_ACCOUNT.money(), response.money());
    }

    @DisplayName("Пополнение счёта")
    @ParameterizedTest(name = "Пополнить на {0}")
    @MethodSource("provideMoneyForAccountOperations")
    void testTopUpAccount(Money money) {
        // Arrange
        var accountDao = mockAccountDao();
        var service = new AccountService(accountDao, mock());
        var number = DEFAULT_ACCOUNT.number();
        var sum = DEFAULT_ACCOUNT.money().add(money);

        // Act
        var response = service.topUpAccount(number, money);

        // Assert
        if (money.isEmpty()) {
            verify(accountDao, never()).updateMoney(anyLong(), any());
        } else {
            verify(accountDao).updateMoney(number, sum);
        }
        assertEquals(
                new Account(number, DEFAULT_ACCOUNT.currency(), sum),
                response);
    }

    @DisplayName("Снятие денег со счёта")
    @ParameterizedTest(name = "Снять {0}")
    @MethodSource("provideMoneyForAccountOperations")
    void testWithdrawMoneyToAccount(Money money) {
        // Arrange
        var accountDao = mockAccountDao();
        var service = new AccountService(accountDao, mock());
        var number = DEFAULT_ACCOUNT.number();
        var sub = DEFAULT_ACCOUNT.money().subtract(money);

        // Act
        var response = service.withdrawMoneyToAccount(number, money);

        // Assert
        if (money.isEmpty()) {
            verify(accountDao, never()).updateMoney(anyLong(), any());
        } else {
            verify(accountDao).updateMoney(number, sub);
        }
        assertEquals(
                new Account(number, DEFAULT_ACCOUNT.currency(), sub),
                response);
    }

    @DisplayName("Ошибка при снятии денег больше, чем есть на счету")
    @Test
    void testWithdrawMoreMoneyToAccountFailed() {
        // Arrange
        var service = new AccountService(mockAccountDao(), mock());
        var number = DEFAULT_ACCOUNT.number();
        var money = Money.of(BigDecimal.TWO).add(DEFAULT_ACCOUNT.money());

        // Act & Assert
        assertThrows(InvalidQueryException.class, () -> service.withdrawMoneyToAccount(number, money));
    }

    private static Stream<Money> provideMoneyForAccountOperations() {
        return Stream.of(
                Money.empty(),
                Money.of(new BigDecimal("1943.07")),
                Money.of(new BigDecimal("0.03")));
    }

    private static AccountDao mockAccountDao() {
        var dao = mock(AccountDao.class);
        when(dao.existsByNumber(anyLong())).thenReturn(false);
        when(dao.existsByNumber(DEFAULT_ACCOUNT.number())).thenReturn(true);
        when(dao.getByNumber(DEFAULT_ACCOUNT.number())).thenReturn(DEFAULT_ACCOUNT);
        return dao;
    }

    private static CustomerDao mockCustomerDao() {
        var dao = mock(CustomerDao.class);
        when(dao.existsByNickname(anyString())).thenReturn(false);
        when(dao.existsByNickname(DEFAULT_CUSTOMER.nickname())).thenReturn(true);
        when(dao.getByNickname(DEFAULT_CUSTOMER.nickname())).thenReturn(DEFAULT_CUSTOMER);
        return dao;
    }
}
