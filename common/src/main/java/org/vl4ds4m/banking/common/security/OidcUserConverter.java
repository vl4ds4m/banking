package org.vl4ds4m.banking.common.security;

import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.JWTParser;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.oauth2.client.OAuth2LoginConfigurer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserSource;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUserAuthority;
import org.springframework.util.StringUtils;

import java.text.ParseException;
import java.util.*;

public class OidcUserConverter implements Converter<OidcUserSource, OidcUser> {

    public static void apply(OAuth2LoginConfigurer<HttpSecurity>.UserInfoEndpointConfig uie) {
        var service = new OidcUserService();
        var converter = new OidcUserConverter();
        service.setOidcUserConverter(converter);

        uie.oidcUserService(service);
    }

    @Override
    public OidcUser convert(OidcUserSource source) {
        OidcUserRequest userRequest = source.getUserRequest();
        OidcUserInfo userInfo = source.getUserInfo();
        Set<GrantedAuthority> authorities = new LinkedHashSet<>();
        ClientRegistration.ProviderDetails providerDetails = userRequest.getClientRegistration().getProviderDetails();
        String userNameAttributeName = providerDetails.getUserInfoEndpoint().getUserNameAttributeName();
        if (StringUtils.hasText(userNameAttributeName)) {
            authorities.add(new OidcUserAuthority(userRequest.getIdToken(), userInfo, userNameAttributeName));
        }
        else {
            authorities.add(new OidcUserAuthority(userRequest.getIdToken(), userInfo));
        }
        OAuth2AccessToken token = userRequest.getAccessToken();
        for (String scope : token.getScopes()) {
            authorities.add(new SimpleGrantedAuthority("SCOPE_" + scope));
        }
        Collection<String> roles = getRoles(token.getTokenValue());
        for (String scope : roles) {
            authorities.add(new SimpleGrantedAuthority(SecurityRole.PREFIX + scope));
        }
        if (StringUtils.hasText(userNameAttributeName)) {
            return new DefaultOidcUser(authorities, userRequest.getIdToken(), userInfo, userNameAttributeName);
        }
        return new DefaultOidcUser(authorities, userRequest.getIdToken(), userInfo);
    }

    private static Collection<String> getRoles(String token) {
        JWTClaimsSet claimsSet;
        try {
            JWT jwt = JWTParser.parse(token);
            claimsSet = jwt.getJWTClaimsSet();
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        Map<String, Object> claims = claimsSet != null
                ? claimsSet.getClaims()
                : null;
        return JwtRolesConverter.getRealmRoles(claims);
    }

}
