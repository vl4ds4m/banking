package org.vl4ds4m.banking.repository;

import org.springframework.data.repository.CrudRepository;
import org.vl4ds4m.banking.repository.entity.AccountRe;

import java.util.Optional;

public interface AccountRepository extends CrudRepository<AccountRe, Long> {

    Optional<AccountRe> findByNumber(Long number);
}
