package edu.vl4ds4m.banking.dao;

import edu.vl4ds4m.banking.dto.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Integer> {
}
