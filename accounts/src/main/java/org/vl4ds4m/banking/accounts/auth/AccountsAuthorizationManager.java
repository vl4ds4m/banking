package org.vl4ds4m.banking.accounts.auth;

import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.vl4ds4m.banking.accounts.dao.AccountDao;
import org.vl4ds4m.banking.accounts.entity.Account;
import org.vl4ds4m.banking.accounts.entity.Customer;
import org.vl4ds4m.banking.common.security.SecurityRole;
import org.vl4ds4m.banking.common.util.To;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class AccountsAuthorizationManager {

    private final AccountDao accountDao;

    public void authorizeCustomer(String customerLogin) {
        if (isUserAdmin()) return;

        String actualLogin = getCustomerLogin();
        if (actualLogin == null || !actualLogin.equals(customerLogin)) {
            throw new AuthorizationDeniedException(
                    To.string(Customer.class, actualLogin)
                            + " can't access to another customer resources");
        }
    }

    public void authorizeAccountOwner(long accountNumber) {
        if (isUserAdmin()) return;

        String actualLogin = getCustomerLogin();
        String expectedLogin = accountDao.getOwner(accountNumber).login();
        if (actualLogin == null || !actualLogin.equals(expectedLogin)) {
            throw new AuthorizationDeniedException(
                    To.string(Customer.class, actualLogin)
                            + " isn't owner of "
                            + To.string(Account.class, accountNumber));
        }
    }

    private boolean isUserAdmin() {
        return getAuthentication()
                .getAuthorities()
                .stream()
                .map(a -> a.getAuthority())
                .anyMatch(a -> SecurityRole.ADMIN.toAuthority().equals(a));
    }

    private @Nullable String getCustomerLogin() {
        var attrs = getAuthentication().getTokenAttributes();
        return (String) Optional.ofNullable(attrs)
                .map(claims -> claims.get("preferred_username"))
                .filter(username -> username instanceof String)
                .orElse(null);
    }

    private JwtAuthenticationToken getAuthentication() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth instanceof JwtAuthenticationToken token) return token;
        throw new IllegalStateException("Request must has JwtAuthenticationToken as authentication");
    }

}
