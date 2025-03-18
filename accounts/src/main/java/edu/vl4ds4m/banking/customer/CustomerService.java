package edu.vl4ds4m.banking.customer;

import com.giffing.bucket4j.spring.boot.starter.context.RateLimiting;
import edu.vl4ds4m.banking.account.Account;
import edu.vl4ds4m.banking.converter.ConverterService;
import edu.vl4ds4m.banking.currency.Currency;
import edu.vl4ds4m.banking.customer.dto.CustomerBalanceResponse;
import edu.vl4ds4m.banking.customer.dto.CustomerCreationRequest;
import edu.vl4ds4m.banking.customer.dto.CustomerCreationResponse;
import edu.vl4ds4m.banking.exception.InvalidCustomerIdException;
import edu.vl4ds4m.banking.exception.InvalidDataException;
import edu.vl4ds4m.banking.Conversions;
import io.micrometer.observation.annotation.Observed;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;

@Service
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
        ratePerMethod = true)
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
                    account.getCurrency(), currency, amount);
                balance = balance.add(amount);
            }
        }
        return new CustomerBalanceResponse(balance, currency);
    }
}
