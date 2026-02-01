package org.vl4ds4m.banking.common.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration("commonSecurityConfig")
public class SecurityConfig {

    private static final String[] commonPermittedPatterns = {
            "/error",
            "/favicon.ico",
            "/actuator/**"};

    @Bean
    @Order(1)
    public SecurityFilterChain commonPermittedPatternsSecurityFilterChain(HttpSecurity http) {
        http.securityMatcher(commonPermittedPatterns);
        return http.build();
    }

}
