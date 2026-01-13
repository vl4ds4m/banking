package org.vl4ds4m.banking.converter.api.grpc;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.grpc.server.GlobalServerInterceptor;
import org.springframework.grpc.server.security.AuthenticationProcessInterceptor;
import org.springframework.grpc.server.security.GrpcSecurity;
import org.vl4ds4m.banking.converter.grpc.ConverterGrpc;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration("grpcSecurityConfig")
public class SecurityConfig {

    private static final String CONVERTER_PREFIX = ConverterGrpc.SERVICE_NAME + '/';

    @Bean
    @GlobalServerInterceptor
    public AuthenticationProcessInterceptor authenticationProcessInterceptor(GrpcSecurity grpc) throws Exception {
        grpc.authorizeRequests(authorizeRequests -> authorizeRequests
                .methods(CONVERTER_PREFIX + "Convert").authenticated()
                .allRequests().denyAll());

        grpc.oauth2ResourceServer(oauth2ResourceServer -> oauth2ResourceServer
                .jwt(withDefaults()));
        return grpc.build();
    }

}
