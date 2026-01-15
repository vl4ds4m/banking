package org.vl4ds4m.banking.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.config.core.GrantedAuthorityDefaults;

@Configuration("commonSecurityConfig")
public class SecurityConfig {

    private static final String ROLE_PREFIX = "SCOPE_";

    public static String role(String name) {
        return ROLE_PREFIX + name;
    }

    @Bean
    public static GrantedAuthorityDefaults grantedAuthorityDefaults() {
        return new GrantedAuthorityDefaults(ROLE_PREFIX);
    }

    @Bean
    public RoleHierarchy roleHierarchy() {
        return RoleHierarchyImpl.withRolePrefix(ROLE_PREFIX)
                .role("banking-admin").implies("converter-user", "rates-user")
                .build();
    }

}
