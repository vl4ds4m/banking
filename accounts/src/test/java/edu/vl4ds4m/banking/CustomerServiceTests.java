package edu.vl4ds4m.banking;

import edu.vl4ds4m.banking.account.Account;
import edu.vl4ds4m.banking.converter.ConverterService;
import edu.vl4ds4m.banking.currency.Currency;
import edu.vl4ds4m.banking.customer.Customer;
import edu.vl4ds4m.banking.customer.CustomerRepository;
import edu.vl4ds4m.banking.customer.CustomerService;
import edu.vl4ds4m.banking.customer.dto.CustomerBalanceResponse;
import edu.vl4ds4m.banking.customer.dto.CustomerCreationRequest;
import edu.vl4ds4m.banking.customer.dto.CustomerCreationResponse;
import edu.vl4ds4m.banking.exception.InvalidDataException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CustomerServiceTests {
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
