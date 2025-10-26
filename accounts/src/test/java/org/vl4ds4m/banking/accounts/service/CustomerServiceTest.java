package org.vl4ds4m.banking.accounts.service;

/*import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.lang.NonNull;
import org.vl4ds4m.banking.accounts.api.model.CreateCustomerRequest;
import org.vl4ds4m.banking.accounts.api.util.CurrencyConverter;
import org.vl4ds4m.banking.common.entity.Currency;
import org.vl4ds4m.banking.accounts.entity.Customer;
import org.vl4ds4m.banking.common.entity.Money;
import org.vl4ds4m.banking.accounts.repository.CustomerRepository;
import org.vl4ds4m.banking.accounts.repository.entity.AccountRe;
import org.vl4ds4m.banking.accounts.repository.entity.CustomerRe;
import org.vl4ds4m.banking.accounts.service.expection.DuplicateEntityException;
import org.vl4ds4m.banking.accounts.service.expection.EntityNotFoundException;
import org.vl4ds4m.banking.accounts.service.expection.ServiceException;
import org.vl4ds4m.banking.accounts.util.TestEntity;
import org.vl4ds4m.banking.accounts.util.TestRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;*/

// ToDo repair
class CustomerServiceTest {
/*
    public static final Customer DEFAULT_CUSTOMER = TestEntity.createDefaultCustomer();

    @DisplayName("Получение клиента по имени")
    @Test
    void testGetCustomerByName() {
        // Arrange
        var service = new CustomerService(fakeCustomerRepository(), null);

        // Act
        var customer = service.getCustomerByName(DEFAULT_CUSTOMER.name());

        // Arrange
        assertEquals(DEFAULT_CUSTOMER, customer);
    }

    @DisplayName("Ошибка при запросе несуществующего клиента")
    @Test
    void testGetAbsentCustomerFailed() {
        // Arrange
        var service = new CustomerService(fakeCustomerRepository(), null);

        // Act & Assert
        var e = assertThrows(EntityNotFoundException.class,
                () -> service.getCustomerByName("unregistered_client"));
        assertEquals("Customer[name=unregistered_client] not found", e.getMessage());
    }

    @DisplayName("Создание клиента")
    @Test
    void testCreateCustomer() {
        // Arrange
        var service = new CustomerService(fakeCustomerRepository(), null);
        var customerName = "foo_bar";
        var birthDate = LocalDate.now().minusYears(45);
        var request = new CreateCustomerRequest(customerName, "Foo", "Bar", birthDate);

        // Act
        service.createCustomer(request);

        // Assert
        var customer = service.getCustomerByName(customerName);
        assertEquals(customerName, customer.name());
        assertEquals("Foo", customer.firstName());
        assertEquals("Bar", customer.lastName());
        assertEquals(birthDate, customer.birthDate());
    }

    @DisplayName("Ошибка при создании клиента с недопустимым возрастом")
    @ParameterizedTest(name = "День рождения: {0}")
    @MethodSource("provideCustomerBirthDates")
    void testCreateCustomerWithInvalidAgeFailed(LocalDate birthDate) {
        // Arrange
        var service = new CustomerService(fakeCustomerRepository(), null);
        var request = new CreateCustomerRequest("strange_client", "Benjamin", "Button", birthDate);

        // Act & Assert
        var e = assertThrows(ServiceException.class, () -> service.createCustomer(request));
        assertEquals(
                "Customer age must be in range of 14 to 120 years. " +
                        "Passed birth date = " + birthDate,
                e.getMessage());
    }

    @DisplayName("Ошибка при создании клиента с уже занятым именем")
    @Test
    void testCreateCustomerWithExistedNameFailed() {
        // Arrange
        var service = new CustomerService(fakeCustomerRepository(), null);
        var request = new CreateCustomerRequest(
                DEFAULT_CUSTOMER.name(),
                "Yet",
                "Another",
                DEFAULT_CUSTOMER.birthDate());

        // Act & Assert
        var e = assertThrows(DuplicateEntityException.class, () -> service.createCustomer(request));
        assertEquals("Customer[name=" + DEFAULT_CUSTOMER.name() + "] already exists", e.getMessage());
    }

    @DisplayName("Получение баланса по всем счетам клиента")
    @Test
    void testGetBalance() {
        // Arrange
        var customerRepository = fakeCustomerRepository();

        var customer = customerRepository.findByName(DEFAULT_CUSTOMER.name()).orElseThrow();

        var account1 = new AccountRe();
        account1.setId(1L);
        account1.setNumber(101L);
        account1.setCustomer(customer);
        account1.setCurrency(Currency.RUB);
        var money1 = Money.of(new BigDecimal("452.87"));
        account1.setAmount(money1.amount());

        var account2 = new AccountRe();
        account2.setId(2L);
        account2.setNumber(202L);
        account2.setCustomer(customer);
        account2.setCurrency(Currency.EUR);
        var money2 = Money.of(new BigDecimal("16.02"));
        account2.setAmount(money2.amount());

        customer.setAccounts(Set.of(account1, account2));

        customerRepository.save(customer);

        ConverterService converterService = mock();
        var convertedMoney1 = Money.of(new BigDecimal("12"));
        when(converterService.convert(Currency.RUB, Currency.USD, money1))
                .thenReturn(convertedMoney1);
        var convertedMoney2 = Money.of(new BigDecimal("18"));
        when(converterService.convert(Currency.EUR, Currency.USD, money2))
                .thenReturn(convertedMoney2);

        var totalCurrency = CurrencyConverter.toApi(Currency.USD);

        var service = new CustomerService(customerRepository, converterService);

        // Act
        var balance = service.getCustomerBalance(DEFAULT_CUSTOMER.name(), totalCurrency);

        // Assert
        assertEquals(totalCurrency, balance.getCurrency());
        assertEquals(
                convertedMoney1.add(convertedMoney2).amount(),
                balance.getAmount());
    }

    private static CustomerRepository fakeCustomerRepository() {
        CustomerRepository repository = new CustomerTestRepository();

        var customer = new CustomerRe();
        customer.setName(DEFAULT_CUSTOMER.name());
        customer.setFirstName(DEFAULT_CUSTOMER.firstName());
        customer.setLastName(DEFAULT_CUSTOMER.lastName());
        customer.setBirthDate(DEFAULT_CUSTOMER.birthDate());

        repository.save(customer);

        return repository;
    }

    private static Stream<LocalDate> provideCustomerBirthDates() {
        var now = LocalDate.now();
        return Stream.of(now.minusYears(200), now.minusYears(10));
    }

    private static class CustomerTestRepository
            extends TestRepository<CustomerRe, Long>
            implements CustomerRepository
    {
        private final AtomicLong nextId = new AtomicLong(0L);

        @Override
        @NonNull
        protected Optional<Long> extractId(@NonNull CustomerRe entity) {
            return Optional.ofNullable(entity.getId());
        }

        @Override
        protected void setId(@NonNull Long id, @NonNull CustomerRe entity) {
            entity.setId(id);
        }

        @Override
        @NonNull
        protected Long produceNextId() {
            return nextId.incrementAndGet();
        }

        @Override
        public Optional<CustomerRe> findByName(String name) {
            for (var customer : getAll()) {
                if (customer.getName().equals(name)) {
                    return Optional.of(extract(customer.getId()));
                }
            }
            return Optional.empty();
        }
    }*/
}
