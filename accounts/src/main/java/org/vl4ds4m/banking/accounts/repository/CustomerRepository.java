package org.vl4ds4m.banking.accounts.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.vl4ds4m.banking.accounts.repository.model.CustomerPe;

import java.util.Optional;

@Repository
public interface CustomerRepository extends CrudRepository<CustomerPe, String> {

    Optional<CustomerPe> findByName(String name);
}
