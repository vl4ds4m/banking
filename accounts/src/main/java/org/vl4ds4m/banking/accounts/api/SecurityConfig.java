package org.vl4ds4m.banking.accounts.api;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
public class SecurityConfig {

    public static final String OAUTH2_CLIENT_REG = "accounts-client-reg";

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) {
        http.authorizeHttpRequests(authorizeHttpRequests -> authorizeHttpRequests
                .anyRequest().permitAll());

        http.csrf(csrf -> csrf.disable());

        http.oauth2Client(withDefaults());

        return http.build();
    }

}
