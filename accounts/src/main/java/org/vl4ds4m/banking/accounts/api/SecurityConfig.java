package org.vl4ds4m.banking.accounts.api;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.vl4ds4m.banking.accounts.openapi.server.api.AccountsApi;
import org.vl4ds4m.banking.accounts.openapi.server.api.CustomersApi;
import org.vl4ds4m.banking.accounts.openapi.server.api.TransferApi;
import org.vl4ds4m.banking.common.security.JwtGrantedAuthoritiesCompositeConverter;
import org.vl4ds4m.banking.common.security.SecurityRole;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) {
        http.authorizeHttpRequests(authorizeHttpRequests -> {
            requestCustomers(authorizeHttpRequests);
            requestAccounts(authorizeHttpRequests);
            requestTransfer(authorizeHttpRequests);
            authorizeHttpRequests.anyRequest().denyAll();
        });

        http.csrf(csrf -> csrf.disable()); // TODO enable and configure

        http.oauth2ResourceServer(oauth2ResourceServer -> oauth2ResourceServer
                .jwt(JwtGrantedAuthoritiesCompositeConverter::apply));

        http.oauth2Client(withDefaults());

        return http.build();
    }

    private static void requestCustomers(
            AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry registry
    ) {
        registry
                .requestMatchers(
                        CustomersApi.PATH_CREATE_CUSTOMER,
                        CustomersApi.PATH_GET_CUSTOMERS)
                .hasRole(SecurityRole.ADMIN.toString())

                .requestMatchers(
                        CustomersApi.PATH_GET_CUSTOMER_INFO,
                        CustomersApi.PATH_GET_CUSTOMER_BALANCE)
                .authenticated();
    }

    private static void requestAccounts(
            AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry registry
    ) {
        registry
                .requestMatchers(
                        AccountsApi.PATH_TOP_UP_ACCOUNT,
                        AccountsApi.PATH_WITHDRAW_ACCOUNT)
                .hasRole(SecurityRole.ACCOUNTS_OPERATOR.toString())

                .requestMatchers(
                        AccountsApi.PATH_CREATE_ACCOUNT,
                        AccountsApi.PATH_GET_ACCOUNT_INFO,
                        AccountsApi.PATH_GET_ACCOUNT_NUMBER_BY_CUSTOMER)
                .authenticated();
    }

    private static void requestTransfer(
            AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry registry
    ) {
        registry
                .requestMatchers(TransferApi.PATH_TRANSFER)
                .authenticated();
    }

}
