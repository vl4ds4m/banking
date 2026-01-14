package org.vl4ds4m.banking.common.handler.auth;

import io.grpc.*;
import io.grpc.ForwardingClientCall.SimpleForwardingClientCall;
import org.jspecify.annotations.Nullable;
import org.springframework.grpc.server.security.GrpcSecurity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.client.*;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;

import java.util.function.Supplier;

public class OAuth2ClientGrpcInterceptor implements ClientInterceptor {

    private static final Authentication ANONYMOUS_AUTHENTICATION = new AnonymousAuthenticationToken(
            "anonymous", "anonymousUser",
            AuthorityUtils.createAuthorityList("ROLE_ANONYMOUS"));

    private final OAuth2AuthorizedClientManager authorizedClientManager;

    private final Supplier<@Nullable String> clientRegistrationId;

    public OAuth2ClientGrpcInterceptor(
            ClientRegistrationRepository clientRegistrationRepository,
            OAuth2AuthorizedClientService authorizedClientService,
            Supplier<@Nullable String> clientRegistrationId
    ) {
        this.authorizedClientManager = new AuthorizedClientServiceOAuth2AuthorizedClientManager(
                clientRegistrationRepository,
                authorizedClientService);
        this.clientRegistrationId = clientRegistrationId;
    }

    @Override
    public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(
            MethodDescriptor<ReqT, RespT> method,
            CallOptions callOptions,
            Channel next
    ) {
        ClientCall<ReqT, RespT> clientCall = next.newCall(method, callOptions);
        String token = authorize();
        if (token == null) {
            return clientCall;
        }
        return new OAuth2ClientForwardingClientCall<>(clientCall, token);
    }

    private @Nullable String authorize() {
        String clientRegId = clientRegistrationId.get();
        if (clientRegId == null) {
            return null;
        }

        OAuth2AuthorizeRequest authorizeRequest = OAuth2AuthorizeRequest.withClientRegistrationId(clientRegId)
                .principal(ANONYMOUS_AUTHENTICATION)
                .build();
        OAuth2AuthorizedClient client = authorizedClientManager.authorize(authorizeRequest);

        if (client == null) {
            return null;
        }
        return client.getAccessToken().getTokenValue();
    }

    private static class OAuth2ClientForwardingClientCall<ReqT, RespT>
            extends SimpleForwardingClientCall<ReqT, RespT>
    {

        final String token;

        OAuth2ClientForwardingClientCall(ClientCall<ReqT, RespT> delegate, String token) {
            super(delegate);
            this.token = token;
        }

        @Override
        public void start(Listener<RespT> responseListener, Metadata headers) {
            headers.put(GrpcSecurity.AUTHORIZATION_KEY, "Bearer " + token);
            super.start(responseListener, headers);
        }

    }

}
