package org.vl4ds4m.banking.accounts.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.validation.Errors;
import org.springframework.validation.SimpleErrors;
import org.vl4ds4m.banking.accounts.dao.CustomerDao;
import org.vl4ds4m.banking.accounts.entity.Account;
import org.vl4ds4m.banking.accounts.entity.Customer;
import org.vl4ds4m.banking.accounts.service.expection.DuplicateEntityException;
import org.vl4ds4m.banking.accounts.service.expection.EntityNotFoundException;
import org.vl4ds4m.banking.accounts.service.validator.CustomerValidator;
import org.vl4ds4m.banking.accounts.util.TestEntity;
import org.vl4ds4m.banking.common.entity.Currency;
import org.vl4ds4m.banking.common.entity.Money;
import org.vl4ds4m.banking.common.exception.ValidationException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CustomerServiceTest {

    public static final Customer DEFAULT_CUSTOMER = TestEntity.createDefaultCustomer();

    @DisplayName("Получение клиента по nickname")
    @Test
    void testGetCustomerByNickname() {
        // Arrange
        var service = createCustomerService();

        // Act
        var customer = service.getCustomer(DEFAULT_CUSTOMER.nickname());

        // Arrange
        assertEquals(DEFAULT_CUSTOMER, customer);
    }

    @DisplayName("Ошибка при запросе несуществующего клиента")
    @Test
    void testGetAbsentCustomerFailed() {
        // Arrange
        var customerNickname = "unregistered_client";
        var service = createCustomerService();

        // Act & Assert
        var e = assertThrows(EntityNotFoundException.class, () -> service.getCustomer(customerNickname));
        assertTrue(e.getMessage().contains(customerNickname));
    }

    @DisplayName("Создание клиента")
    @Test
    void testCreateCustomer() {
        // Arrange
        var customer = new Customer("foo_bar", "Foo", "Bar", LocalDate.now().minusYears(45));

        var customerDao = mockCustomerDao();

        var errors = mock(Errors.class);
        when(errors.hasErrors()).thenReturn(false);

        var customerValidator = mock(CustomerValidator.class);
        when(customerValidator.validateObject(any())).thenReturn(errors);

        var service = new CustomerService(customerDao, customerValidator, mock());

        // Act
        service.createCustomer(customer);

        // Assert
        verify(customerDao).create(customer);
    }

    @DisplayName("Ошибка при создании клиента с некорректными данными")
    @Test
    void testCreateCustomerWithInvalidDataFailed() {
        // Arrange
        var customer = new Customer("invalid_client",
                DEFAULT_CUSTOMER.forename(),
                DEFAULT_CUSTOMER.surname(),
                DEFAULT_CUSTOMER.birthdate());

        var errors = new SimpleErrors(customer);
        errors.rejectValue(CustomerValidator.FORENAME_FIELD, "some.problem", "Some validation error");
        var customerValidator = mock(CustomerValidator.class);
        when(customerValidator.validateObject(customer)).thenReturn(errors);

        var service = new CustomerService(mockCustomerDao(), customerValidator, mock());

        // Act & Assert
        var e = assertThrows(ValidationException.class, () -> service.createCustomer(customer));
        assertEquals(errors, e.getErrors());
    }

    @DisplayName("Ошибка при создании клиента с уже занятым nickname")
    @Test
    void testCreateCustomerWithExistedNicknameFailed() {
        // Arrange
        var customer = new Customer(
                DEFAULT_CUSTOMER.nickname(),
                "Yet",
                "Another",
                DEFAULT_CUSTOMER.birthdate());
        var service = createCustomerService();

        // Act & Assert
        var e = assertThrows(DuplicateEntityException.class, () -> service.createCustomer(customer));
        assertTrue(e.getMessage().contains(DEFAULT_CUSTOMER.nickname()));
    }

    @DisplayName("Получение баланса по всем счетам клиента")
    @Test
    void testGetBalance() {
        // Arrange
        var customerDao = mockCustomerDao();
        var money1 = Money.of(new BigDecimal("452.87"));
        var money2 = Money.of(new BigDecimal("16.02"));
        var accounts = Set.of(
                new Account(101L, Currency.RUB, money1),
                new Account(202L, Currency.EUR, money2));
        when(customerDao.getAccounts(DEFAULT_CUSTOMER.nickname())).thenReturn(accounts);

        var totalCurrency = Currency.USD;
        var converterService = mock(ConverterService.class);
        var convertedMoney1 = Money.of(new BigDecimal("12"));
        when(converterService.convert(Currency.RUB, totalCurrency, money1))
                .thenReturn(convertedMoney1);
        var convertedMoney2 = Money.of(new BigDecimal("18"));
        when(converterService.convert(Currency.EUR, totalCurrency, money2))
                .thenReturn(convertedMoney2);

        var service = new CustomerService(customerDao, mock(), converterService);

        // Act
        var balance = service.getCustomerBalance(DEFAULT_CUSTOMER.nickname(), totalCurrency);

        // Assert
        assertEquals(convertedMoney1.add(convertedMoney2), balance);
    }

    private static CustomerService createCustomerService() {
        return new CustomerService(mockCustomerDao(), mock(), mock());
    }

    private static CustomerDao mockCustomerDao() {
        var dao = mock(CustomerDao.class);
        when(dao.existsByNickname(anyString())).thenReturn(false);
        when(dao.existsByNickname(DEFAULT_CUSTOMER.nickname())).thenReturn(true);
        when(dao.getByNickname(DEFAULT_CUSTOMER.nickname())).thenReturn(DEFAULT_CUSTOMER);
        return dao;
    }
}
