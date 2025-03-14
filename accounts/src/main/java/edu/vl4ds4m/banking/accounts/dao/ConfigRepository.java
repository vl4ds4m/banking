package edu.vl4ds4m.banking.accounts.dao;

import edu.vl4ds4m.banking.accounts.dto.Config;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConfigRepository extends JpaRepository<Config, Config.Type> {
}
