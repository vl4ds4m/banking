package edu.tinkoff.dao;

import edu.tinkoff.dto.Config;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConfigRepository extends JpaRepository<Config, String> {
}
