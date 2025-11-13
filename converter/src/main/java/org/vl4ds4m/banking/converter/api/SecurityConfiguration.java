package org.vl4ds4m.banking.converter.api;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.vl4ds4m.banking.common.auth.Auth;

@Configuration
@EnableMethodSecurity
public class SecurityConfiguration {
    @Bean
    @Profile("!" + Auth.PROFILE)
    public SecurityFilterChain freeSecurityfilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authz -> authz
                .anyRequest().permitAll());
        return http.build();
    }

    @Bean
    @Profile(Auth.PROFILE)
    public SecurityFilterChain authSecurityfilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authz -> authz
                .anyRequest().authenticated())
            .oauth2ResourceServer(serverConfigurer -> serverConfigurer
                .jwt(jwtConfigurer -> jwtConfigurer
                    .jwtAuthenticationConverter(jwtAuthenticationConverter())));
        return http.build();
    }

    @Bean
    @Profile(Auth.PROFILE)
    public Converter<Jwt, AbstractAuthenticationToken> jwtAuthenticationConverter() {
        return new JwtAuthenticationConverter();
    }
}
