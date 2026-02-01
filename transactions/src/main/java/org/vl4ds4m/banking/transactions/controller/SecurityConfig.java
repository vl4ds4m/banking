package org.vl4ds4m.banking.transactions.controller;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.vl4ds4m.banking.common.security.JwtGrantedAuthoritiesCompositeConverter;
import org.vl4ds4m.banking.common.security.SecurityRole;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) {
        http.sessionManagement(sm -> sm
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.csrf(csrf -> csrf.disable()); // TODO enable and configure

        http.authorizeHttpRequests(authz -> authz
                .requestMatchers("/reread").hasRole(SecurityRole.ADMIN.toString())
                .anyRequest().denyAll());

        http.oauth2ResourceServer(oauth2ResourceServer -> oauth2ResourceServer
                .jwt(JwtGrantedAuthoritiesCompositeConverter::apply));

        return http.build();
    }

}
