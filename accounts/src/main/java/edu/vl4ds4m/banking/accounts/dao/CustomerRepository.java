package edu.vl4ds4m.banking.accounts.dao;

import edu.vl4ds4m.banking.accounts.dto.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Integer> {
}
