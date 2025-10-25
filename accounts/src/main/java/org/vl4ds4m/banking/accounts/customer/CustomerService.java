package org.vl4ds4m.banking.accounts.customer;

import com.giffing.bucket4j.spring.boot.starter.context.RateLimitException;
import com.giffing.bucket4j.spring.boot.starter.context.RateLimiting;
import io.micrometer.observation.annotation.Observed;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.annotation.Validated;
import org.vl4ds4m.banking.common.Conversions;
import org.vl4ds4m.banking.accounts.account.Account;
import org.vl4ds4m.banking.accounts.api.model.Currency;
import org.vl4ds4m.banking.accounts.converter.ConverterService;
import org.vl4ds4m.banking.accounts.customer.dto.CustomerBalanceResponse;
import org.vl4ds4m.banking.accounts.customer.dto.CustomerCreationRequest;
import org.vl4ds4m.banking.accounts.customer.dto.CustomerCreationResponse;
import org.vl4ds4m.banking.accounts.exception.InvalidCustomerIdException;
import org.vl4ds4m.banking.accounts.exception.InvalidDataException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;

// @Service
@Validated
public class CustomerService {
    private static final Logger logger = LoggerFactory.getLogger(CustomerService.class);

    private final CustomerRepository customerRepository;
    private final ConverterService converterService;

    public CustomerService(CustomerRepository customerRepository, ConverterService converterService) {
        this.customerRepository = customerRepository;
        this.converterService = converterService;
    }

    @Observed
    public CustomerCreationResponse createCustomer(@Valid CustomerCreationRequest request) {
        LocalDate now = LocalDate.now();
        LocalDate maxBirthDate = now.minusYears(14);
        LocalDate minBirthDate = now.minusYears(121).plusDays(1);
        LocalDate birthDate = request.birthDate();
        if (birthDate.isAfter(maxBirthDate) || birthDate.isBefore(minBirthDate)) {
            throw new InvalidDataException("Customer age must be between 14 and 120");
        }

        Customer customer = new Customer(
            request.firstName(),
            request.lastName(),
            request.birthDate());
        int customerId = customerRepository.save(customer).getId();
        logger.debug("Customer[id={}] created", customerId);
        return new CustomerCreationResponse(customerId);
    }

    public Optional<Customer> findById(int id) {
        return customerRepository.findById(id);
    }

    @RateLimiting(
        name = "customer-balance",
        cacheKey = "#id",
        ratePerMethod = true,
        fallbackMethodName = "exceedRateOnBalance")
    @Observed
    public CustomerBalanceResponse getBalance(int id, Currency currency) {
        Customer customer = findById(id)
            .orElseThrow(() -> new InvalidCustomerIdException(id));

        logger.debug("Return Customer[id={}] balance", customer.getId());
        Set<Account> accounts = customer.getAccounts();
        BigDecimal balance = Conversions.ZERO;
        for (Account account : accounts) {
            BigDecimal amount = account.getAmount();
            if (Conversions.ZERO.compareTo(amount) < 0) {
                amount = converterService.convert(
                    Currency.fromValue(account.getCurrency()), currency, amount);
                balance = balance.add(amount);
            }
        }
        return new CustomerBalanceResponse(balance, currency.getValue());
    }

    public CustomerBalanceResponse exceedRateOnBalance(int id, Currency currency) {
        throw new RateLimitException() {
            @Override
            public String getMessage() {
                return String.format(
                    "Too many requests of Customer[id=%d] balance", id);
            }
        };
    }
}
