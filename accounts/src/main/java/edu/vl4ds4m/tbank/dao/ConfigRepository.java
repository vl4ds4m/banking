package edu.vl4ds4m.tbank.dao;

import edu.vl4ds4m.tbank.dto.Config;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConfigRepository extends JpaRepository<Config, Config.Type> {
}
