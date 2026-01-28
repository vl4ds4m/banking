package org.vl4ds4m.banking.accounts.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.validation.Errors;
import org.springframework.validation.SimpleErrors;
import org.vl4ds4m.banking.accounts.dao.CustomerDao;
import org.vl4ds4m.banking.accounts.entity.Account;
import org.vl4ds4m.banking.accounts.entity.Customer;
import org.vl4ds4m.banking.accounts.service.exception.DuplicateEntityException;
import org.vl4ds4m.banking.accounts.service.exception.EntityNotFoundException;
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

    @DisplayName("Получение клиента по логину")
    @Test
    void testGetCustomerByLogin() {
        // Arrange
        var service = createCustomerService();

        // Act
        var customer = service.getCustomer(DEFAULT_CUSTOMER.login());

        // Arrange
        assertEquals(DEFAULT_CUSTOMER, customer);
    }

    @DisplayName("Ошибка при запросе несуществующего клиента")
    @Test
    void testGetAbsentCustomerFailed() {
        // Arrange
        var customerLogin = "unregistered_client";
        var service = createCustomerService();

        // Act & Assert
        var e = assertThrows(EntityNotFoundException.class, () -> service.getCustomer(customerLogin));
        assertTrue(e.getMessage().contains(customerLogin));
    }

    @DisplayName("Получение всех клиентов")
    @Test
    void testGetAllCustomers() {
        // Arrange
        var customerDao = mockCustomerDao();
        when(customerDao.getAll()).thenReturn(Set.of(DEFAULT_CUSTOMER));
        var service = new CustomerService(customerDao, mock(), mock());

        // Act
        var customers = service.getCustomers();

        // Assert
        assertEquals(Set.of(DEFAULT_CUSTOMER), customers);
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

    @DisplayName("Ошибка при создании клиента с уже занятым логином")
    @Test
    void testCreateCustomerWithExistedLoginFailed() {
        // Arrange
        var customer = new Customer(
                DEFAULT_CUSTOMER.login(),
                "Yet",
                "Another",
                DEFAULT_CUSTOMER.birthdate());
        var service = createCustomerService();

        // Act & Assert
        var e = assertThrows(DuplicateEntityException.class, () -> service.createCustomer(customer));
        assertTrue(e.getMessage().contains(DEFAULT_CUSTOMER.login()));
    }

    @DisplayName("Получение счетов клиента")
    @Test
    void testGetCustomerAccounts() {
        // Arrange
        var customerDao = mockCustomerDao();
        var accounts = Set.of(
                new Account(94L, Currency.RUB, Money.of(new BigDecimal("19.05"))),
                new Account(346L, Currency.EUR, Money.of(new BigDecimal("6.72"))));
        when(customerDao.getAccounts(DEFAULT_CUSTOMER.login())).thenReturn(accounts);

        var service = new CustomerService(customerDao, mock(), mock());

        // Act
        var actual = service.getCustomerAccounts(DEFAULT_CUSTOMER.login());

        // Assert
        assertEquals(accounts, actual);
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
        when(customerDao.getAccounts(DEFAULT_CUSTOMER.login())).thenReturn(accounts);

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
        var balance = service.getCustomerBalance(DEFAULT_CUSTOMER.login(), totalCurrency);

        // Assert
        assertEquals(convertedMoney1.add(convertedMoney2), balance);
    }

    private static CustomerService createCustomerService() {
        return new CustomerService(mockCustomerDao(), mock(), mock());
    }

    private static CustomerDao mockCustomerDao() {
        var dao = mock(CustomerDao.class);
        when(dao.existsByLogin(anyString())).thenReturn(false);
        when(dao.existsByLogin(DEFAULT_CUSTOMER.login())).thenReturn(true);
        when(dao.getByLogin(DEFAULT_CUSTOMER.login())).thenReturn(DEFAULT_CUSTOMER);
        return dao;
    }
}
