package edu.vl4ds4m.tbank.dao;

import edu.vl4ds4m.tbank.dto.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Integer> {
}
