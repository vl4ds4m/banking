package org.vl4ds4m.banking.accounts.repository;

import org.springframework.data.repository.CrudRepository;
import org.vl4ds4m.banking.accounts.repository.model.AccountPe;

public interface AccountRepository extends CrudRepository<AccountPe, Long> {}
