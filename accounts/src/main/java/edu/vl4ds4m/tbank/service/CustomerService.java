package edu.vl4ds4m.tbank.service;

import com.giffing.bucket4j.spring.boot.starter.context.RateLimiting;
import edu.vl4ds4m.tbank.dao.CustomerRepository;
import edu.vl4ds4m.tbank.dto.*;
import edu.vl4ds4m.tbank.exception.InvalidCustomerIdException;
import edu.vl4ds4m.tbank.exception.InvalidDataException;
import edu.vl4ds4m.tbank.util.Conversions;
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
        LocalDate minBirthDate = now.minusYears(120);
        LocalDate birthDate = request.birthDate();
        if (birthDate.isAfter(maxBirthDate) || birthDate.isBefore(minBirthDate)) {
            throw new InvalidDataException("Customer age must be between 14 and 120");
        }

        Customer customer = new Customer();
        customer.setFirstName(request.firstName());
        customer.setLastName(request.lastName());
        customer.setBirthDate(request.birthDate());

        int customerId = customerRepository.save(customer).getId();

        logger.debug("Create Customer[id={}]", customerId);

        return new CustomerCreationResponse(customerId);
    }

    public Optional<Customer> findById(int id) {
        return customerRepository.findById(id);
    }

    @RateLimiting(
            name = "customerBalance",
            cacheKey = "#id",
            ratePerMethod = true
    )
    @Observed
    public CustomerBalanceResponse getBalance(int id, Currency currency) {
        Optional<Customer> customer = customerRepository.findById(id);
        if (customer.isEmpty()) {
            throw new InvalidCustomerIdException(id);
        }

        Set<Account> accounts = customer.get().getAccounts();
        BigDecimal balance = Conversions.setScale(BigDecimal.ZERO);

        for (Account account : accounts) {
            double amount = account.getAmount();
            if (amount > 0) {
                double convertedAmount = converterService.convert(account.getCurrency(), currency, amount);
                balance = balance.add(Conversions.setScale(convertedAmount));
            }
        }
        logger.debug("Return Customer[id={}] balance", customer.get().getId());
        return new CustomerBalanceResponse(balance, currency);
    }
}
