package edu.vl4ds4m.banking.dao;

import edu.vl4ds4m.banking.dto.Config;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConfigRepository extends JpaRepository<Config, Config.Type> {
}
