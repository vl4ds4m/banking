package org.vl4ds4m.banking.repository;

import org.springframework.data.repository.CrudRepository;
import org.vl4ds4m.banking.repository.entity.CustomerRe;

import java.util.Optional;

public interface CustomerRepository extends CrudRepository<CustomerRe, Long> {

    Optional<CustomerRe> findByName(String name);
}
