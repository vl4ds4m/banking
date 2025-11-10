package org.vl4ds4m.banking.accounts.deprecation.admin;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface ConfigRepository extends JpaRepository<ConfigParam, ConfigParam.Key> {
}
