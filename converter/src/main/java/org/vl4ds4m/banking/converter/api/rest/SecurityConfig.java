package org.vl4ds4m.banking.converter.api.rest;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.vl4ds4m.banking.converter.openapi.server.api.ConvertApi;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) {
        http.authorizeHttpRequests(authorizeHttpRequests -> authorizeHttpRequests
                .requestMatchers(ConvertApi.PATH_CONVERT_CURRENCY).authenticated() // TODO has role CONVERTER_USER or ADMIN
                .anyRequest().denyAll());

        http.oauth2ResourceServer(oauth2ResourceServer -> oauth2ResourceServer
                .jwt(withDefaults()));

        return http.build();
    }

}
