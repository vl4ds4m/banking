package edu.tinkoff.spring.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ThreadLocalRandom;

@Configuration
public class RandomConfig {
    @Bean
    public int randomNumber() {
        return ThreadLocalRandom.current().nextInt(1,11);
    }
}
