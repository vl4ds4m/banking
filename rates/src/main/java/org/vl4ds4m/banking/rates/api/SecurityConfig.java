package org.vl4ds4m.banking.rates.api;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.grpc.server.GlobalServerInterceptor;
import org.springframework.grpc.server.security.AuthenticationProcessInterceptor;
import org.springframework.grpc.server.security.GrpcSecurity;
import org.vl4ds4m.banking.common.security.SecurityRole;
import org.vl4ds4m.banking.rates.grpc.RatesGrpc;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
public class SecurityConfig {

    private static final String RATES_PREFIX = RatesGrpc.SERVICE_NAME + '/';

    @Bean
    @GlobalServerInterceptor
    public AuthenticationProcessInterceptor authenticationProcessInterceptor(GrpcSecurity grpc) throws Exception {
        grpc.authorizeRequests(authorizeRequests -> authorizeRequests
                .methods(RATES_PREFIX + "GetRates").hasAuthority(SecurityRole.RATES_USER.toAuthority())
                .methods(RATES_PREFIX + "*").denyAll()
                .allRequests().permitAll());

        grpc.oauth2ResourceServer(oauth2ResourceServer -> oauth2ResourceServer
                .jwt(withDefaults()));

        return grpc.build();
    }

}
