package org.vl4ds4m.banking.accounts.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.vl4ds4m.banking.accounts.dao.AccountDao;
import org.vl4ds4m.banking.accounts.entity.Account;
import org.vl4ds4m.banking.accounts.service.expection.EntityNotFoundException;
import org.vl4ds4m.banking.accounts.service.transaction.TransactionService;
import org.vl4ds4m.banking.common.entity.Currency;
import org.vl4ds4m.banking.common.entity.Money;
import org.vl4ds4m.banking.common.entity.Transaction;
import org.vl4ds4m.banking.common.exception.InvalidQueryException;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class TransferServiceTest {

    private static final Account DEFAULT_SENDER = new Account(
            111L,
            Currency.USD,
            Money.of(new BigDecimal("4376.12")));

    private static final Account DEFAULT_RECEIVER = new Account(
            222L,
            Currency.EUR,
            Money.of(new BigDecimal("27.05")));

    @DisplayName("Перевод денег с одного счёта на другой")
    @Test
    void testTransferMoney() {
        // Arrange
        var money = Money.of(new BigDecimal("17.93"));
        var convertedMoney = Money.of(new BigDecimal("15.84"));

        var accountDao = mockAccountDao();
        var transactionService = mock(TransactionService.class);

        var converterService = mockConverterService();
        when(converterService.convert(DEFAULT_SENDER.currency(), DEFAULT_RECEIVER.currency(), money))
                .thenReturn(convertedMoney);

        var service = new TransferService(accountDao, converterService, transactionService);

        // Act
        var result = service.transferMoney(DEFAULT_SENDER.number(), DEFAULT_RECEIVER.number(), money);

        // Assert
        var senderMoney = DEFAULT_SENDER.money().subtract(money);
        var receiverMoney = DEFAULT_RECEIVER.money().add(convertedMoney);

        verify(accountDao).updateMoney(DEFAULT_SENDER.number(), senderMoney);
        verify(accountDao).updateMoney(DEFAULT_RECEIVER.number(), receiverMoney);

        verify(transactionService).sendTransactions(
                new Transaction(DEFAULT_SENDER.number(), money, true),
                new Transaction(DEFAULT_RECEIVER.number(), convertedMoney, false));

        assertEquals(senderMoney, result.totalSenderMoney());
        assertEquals(receiverMoney, result.totalReceiverMoney());
    }

    @DisplayName("Перевод нулевой суммы с одного счёта на другой")
    @Test
    void testTransferZeroMoney() {
        // Arrange
        var money = Money.empty();
        var accountDao = mockAccountDao();
        var transactionService = mock(TransactionService.class);
        var service = new TransferService(accountDao, mockConverterService(), transactionService);

        // Act
        var result = service.transferMoney(DEFAULT_SENDER.number(), DEFAULT_RECEIVER.number(), money);

        // Assert
        verify(accountDao, never()).updateMoney(anyLong(), any());
        verify(transactionService, never()).sendTransactions(any());

        assertEquals(DEFAULT_SENDER.money(), result.totalSenderMoney());
        assertEquals(DEFAULT_RECEIVER.money(), result.totalReceiverMoney());
    }

    @DisplayName("Ошибка при переводе денег с несуществующего счёта")
    @Test
    void testTransferMoneyFromAbsentAccountFailed() {
        // Arrange
        var service = createTransferService();

        // Act & Assert
        var e = assertThrows(EntityNotFoundException.class,
                () -> service.transferMoney(999L, DEFAULT_RECEIVER.number(), Money.of(new BigDecimal("82.63"))));
        assertTrue(e.getMessage().contains("999"));
    }

    @DisplayName("Ошибка при переводе денег на несуществующий счёт")
    @Test
    void testTransferMoneyToAbsentAccountFailed() {
        // Arrange
        var service = createTransferService();

        // Act & Assert
        var e = assertThrows(EntityNotFoundException.class,
                () -> service.transferMoney(DEFAULT_SENDER.number(), 876L, Money.of(new BigDecimal("94.17"))));
        assertTrue(e.getMessage().contains("876"));
    }

    @DisplayName("Ошибка при нехватке средств для перевода")
    @Test
    void testTransferMoneyOnShortageFailed() {
        // Arrange
        var service = createTransferService();

        // Act & Assert
        var e = assertThrows(InvalidQueryException.class,
                () -> service.transferMoney(
                        DEFAULT_SENDER.number(),
                        DEFAULT_RECEIVER.number(),
                        Money.of(new BigDecimal("98000.25"))));
        assertTrue(e.getMessage().contains("doesn't have enough money"));
    }

    private static AccountDao mockAccountDao() {
        var dao = mock(AccountDao.class);

        when(dao.existsByNumber(anyLong())).thenReturn(false);
        when(dao.existsByNumber(DEFAULT_SENDER.number())).thenReturn(true);
        when(dao.existsByNumber(DEFAULT_RECEIVER.number())).thenReturn(true);

        when(dao.getByNumber(DEFAULT_SENDER.number())).thenReturn(DEFAULT_SENDER);
        when(dao.getByNumber(DEFAULT_RECEIVER.number())).thenReturn(DEFAULT_RECEIVER);

        return dao;
    }

    private static ConverterService mockConverterService() {
        return mock(ConverterService.class);
    }

    private static TransferService createTransferService() {
        return new TransferService(mockAccountDao(), mockConverterService(), mock());
    }
}
