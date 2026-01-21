package org.vl4ds4m.banking.common.security;

import org.jspecify.annotations.Nullable;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.*;

public final class JwtRolesConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

    @Override
    public Collection<GrantedAuthority> convert(Jwt source) {
        return getRealmRoles(source.getClaims())
                .stream()
                .map(a -> SecurityRole.PREFIX + a)
                .map(SimpleGrantedAuthority::new)
                .map(a -> (GrantedAuthority) a)
                .toList();
    }

    @SuppressWarnings("unchecked")
    public static Collection<String> getRealmRoles(@Nullable Map<String, Object> claims) {
        return Optional.ofNullable(claims)
                .map(claim -> claim.get("realm_access"))
                .map(claim -> claim instanceof Map ? (Map<String, Object>) claim : null)
                .map(realmAccess -> realmAccess.get("roles"))
                .map(claim -> claim instanceof Collection ? (Collection<String>) claim : null)
                .orElseGet(List::of);
    }

}
