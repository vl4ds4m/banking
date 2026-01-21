package org.vl4ds4m.banking.common.security;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public final class JwtGrantedAuthoritiesCompositeConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

    private final JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();

    private final JwtRolesConverter rolesConverter = new JwtRolesConverter();

    public static void apply(OAuth2ResourceServerConfigurer<HttpSecurity>.JwtConfigurer jwt) {
        var converter = new JwtAuthenticationConverter();
        var authoritiesConverter = new JwtGrantedAuthoritiesCompositeConverter();
        converter.setJwtGrantedAuthoritiesConverter(authoritiesConverter);

        jwt.jwtAuthenticationConverter(converter);
    }

    @Override
    public Collection<GrantedAuthority> convert(Jwt source) {
        Collection<GrantedAuthority> grantedAuthorities = grantedAuthoritiesConverter.convert(source);
        Collection<GrantedAuthority> roles = rolesConverter.convert(source);
        Collection<GrantedAuthority> composite = new ArrayList<>(grantedAuthorities);
        composite.addAll(roles);
        return Collections.unmodifiableCollection(composite);
    }

}
