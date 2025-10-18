package org.vl4ds4m.banking.repository;

import org.springframework.data.repository.CrudRepository;
import org.vl4ds4m.banking.repository.entity.AccountRe;

public interface AccountRepository extends CrudRepository<AccountRe, Long> {}
