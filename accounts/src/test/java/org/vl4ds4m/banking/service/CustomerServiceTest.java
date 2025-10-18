package org.vl4ds4m.banking.service;

import org.junit.jupiter.api.Test;
import org.vl4ds4m.banking.Conversions;
import org.vl4ds4m.banking.account.Account;
import org.vl4ds4m.banking.converter.ConverterService;
import org.vl4ds4m.banking.currency.Currency;
import org.vl4ds4m.banking.customer.Customer;
import org.vl4ds4m.banking.customer.CustomerRepository;
import org.vl4ds4m.banking.customer.CustomerService;
import org.vl4ds4m.banking.customer.dto.CustomerBalanceResponse;
import org.vl4ds4m.banking.customer.dto.CustomerCreationRequest;
import org.vl4ds4m.banking.customer.dto.CustomerCreationResponse;
import org.vl4ds4m.banking.exception.InvalidDataException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CustomerServiceTest {
    private final CustomerRepository repository = mock(CustomerRepository.class);

    private final ConverterService converterService = mock(ConverterService.class);

    private final CustomerService service = new CustomerService(repository, converterService);

    @Test
    void createCustomer() {
        Customer customer = new Customer();
        customer.setId(123);
        when(repository.save(any(Customer.class)))
            .thenReturn(customer);
        CustomerCreationRequest request = new CustomerCreationRequest(
            "Foo", "Bar",
            LocalDate.now().minusYears(45)
        );
        CustomerCreationResponse response = service.createCustomer(request);
        assertEquals(customer.getId(), response.customerId());
    }

    @Test
    void tryCreateCustomerWithInvalidAge() {
        when(repository.save(any(Customer.class)))
            .thenThrow(IllegalStateException.class);
        assertThrows(InvalidDataException.class, () -> {
            CustomerCreationRequest request = new CustomerCreationRequest(
                "Foo", "Bar",
                LocalDate.now().minusYears(1)
            );
            service.createCustomer(request);
        });
        assertThrows(InvalidDataException.class, () -> {
            CustomerCreationRequest request = new CustomerCreationRequest(
                "Foo", "Bar",
                LocalDate.now().minusYears(130)
            );
            service.createCustomer(request);
        });
    }

    @Test
    void getBalance() {
        Customer customer = new Customer();
        customer.setId(111);
        Set<Account> accounts = Set.of(
            new Account(customer, Currency.EUR),
            new Account(customer, Currency.RUB),
            new Account(customer, Currency.USD)
        );

        BigDecimal amount = Conversions.setScale(BigDecimal.TWO);
        accounts.forEach(a -> a.setAmount(amount));

        customer.setAccounts(accounts);
        when(repository.findById(customer.getId()))
            .thenReturn(Optional.of(customer));

        Currency target = Currency.RUB;
        BigDecimal e2r = Conversions.setScale("93.23");
        BigDecimal u2r = Conversions.setScale("74.9");
        BigDecimal r2r = Conversions.setScale("1");
        when(converterService.convert(Currency.EUR, target, amount))
            .thenReturn(e2r);
        when(converterService.convert(Currency.USD, target, amount))
            .thenReturn(u2r);
        when(converterService.convert(Currency.RUB, target, amount))
            .thenReturn(r2r);

        BigDecimal expected = e2r.add(u2r).add(r2r);
        CustomerBalanceResponse response = service.getBalance(customer.getId(), target);
        assertEquals(target, response.currency());
        assertEquals(expected, response.balance());
    }
}
