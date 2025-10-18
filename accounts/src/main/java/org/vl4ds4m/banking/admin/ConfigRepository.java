package org.vl4ds4m.banking.admin;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ConfigRepository extends JpaRepository<ConfigParam, ConfigParam.Key> {
}
