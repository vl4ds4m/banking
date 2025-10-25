package org.vl4ds4m.banking.accounts.customer;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface CustomerRepository extends JpaRepository<Customer, Integer> {
}
