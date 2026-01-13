package org.vl4ds4m.banking.common.handler.auth;

import io.grpc.*;
import io.grpc.ForwardingClientCall.SimpleForwardingClientCall;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.grpc.server.security.GrpcSecurity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;

import java.util.function.Supplier;

@RequiredArgsConstructor
public class OAuth2ClientGrpcInterceptor implements ClientInterceptor {

    private static final Authentication ANONYMOUS_AUTHENTICATION = new AnonymousAuthenticationToken("anonymous",
            "anonymousUser", AuthorityUtils.createAuthorityList("ROLE_ANONYMOUS"));

    private final OAuth2AuthorizedClientManager authorizedClientManager;

    private final Supplier<@Nullable String> clientRegistrationId;

    @Override
    public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(
            MethodDescriptor<ReqT, RespT> method,
            CallOptions callOptions,
            Channel next
    ) {
        ClientCall<ReqT, RespT> delegate = next.newCall(method, callOptions);
        return new OAuth2ClientForwardingClientCall<>(delegate);
    }

    private @Nullable String authorize() {
        String clientRegId = clientRegistrationId.get();
        if (clientRegId == null) {
            return null;
        }

        Authentication principal = getPrincipal();
        if (principal == null) {
            principal = ANONYMOUS_AUTHENTICATION;
        }

        OAuth2AuthorizeRequest authorizeRequest = OAuth2AuthorizeRequest.withClientRegistrationId(clientRegId)
                .principal(principal)
                .build();
        OAuth2AuthorizedClient client = authorizedClientManager.authorize(authorizeRequest);

        if (client == null) {
            return null;
        }
        return client.getAccessToken().getTokenValue();
    }

    private static @Nullable Authentication getPrincipal() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    private class OAuth2ClientForwardingClientCall<ReqT, RespT> extends SimpleForwardingClientCall<ReqT, RespT> {

        OAuth2ClientForwardingClientCall(ClientCall<ReqT, RespT> delegate) {
            super(delegate);
        }

        @Override
        public void start(Listener<RespT> responseListener, Metadata headers) {
            String token = authorize();
            if (token != null) {
                headers.put(GrpcSecurity.AUTHORIZATION_KEY, "Bearer " + token);
            }
            super.start(responseListener, headers);
        }

    }

}
