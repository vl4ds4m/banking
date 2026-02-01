package org.vl4ds4m.banking.webui.api.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.oauth2.client.oidc.web.logout.OidcClientInitiatedLogoutSuccessHandler;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.web.SecurityFilterChain;
import org.vl4ds4m.banking.common.security.OidcUserConverter;
import org.vl4ds4m.banking.common.security.SecurityRole;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final ClientRegistrationRepository clientRegistrationRepository;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) {
        http.authorizeHttpRequests(authorizeHttpRequests -> {
            authorizeHttpRequests
                    .requestMatchers("/").authenticated()
                    .requestMatchers("/css/**").permitAll();

            requestCustomers(authorizeHttpRequests);
            requestAccounts(authorizeHttpRequests);
            requestTransfer(authorizeHttpRequests);

            authorizeHttpRequests.anyRequest().denyAll();
        });

        http.csrf(csrf -> csrf.disable()); // TODO enable and configure

        http.oauth2Login(login -> login
                .userInfoEndpoint(OidcUserConverter::apply));
        http.logout(this::logoutSuccessHandler);
        http.oauth2Client(withDefaults());

        return http.build();
    }

    private void logoutSuccessHandler(LogoutConfigurer<HttpSecurity> logout) {
        var handler = new OidcClientInitiatedLogoutSuccessHandler(clientRegistrationRepository);
        handler.setPostLogoutRedirectUri("{baseUrl}");
        logout.logoutSuccessHandler(handler);
    }

    private static void requestCustomers(
            AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry registry
    ) {
        registry
                .requestMatchers(
                        "/customers",
                        "/customers/new",
                        "/customers/{login}/info")
                .hasRole(SecurityRole.ADMIN.toString())

                .requestMatchers(
                        "/customers/info")
                .authenticated();
    }

    private static void requestAccounts(
            AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry registry
    ) {
        registry
                .requestMatchers(
                        "/accounts/{number}/top-up",
                        "/accounts/{number}/withdraw")
                .hasRole(SecurityRole.ACCOUNTS_OPERATOR.toString())

                .requestMatchers(
                        "/accounts/{number}/transfer")
                .hasRole(SecurityRole.ADMIN.toString())

                .requestMatchers(
                        "/accounts/new")
                .authenticated();
    }

    private static void requestTransfer(
            AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry registry
    ) {
        registry
                .requestMatchers("/transfer")
                .hasRole(SecurityRole.ADMIN.toString())

                .requestMatchers("/transfer/{number}")
                .authenticated();
    }

}
