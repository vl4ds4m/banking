package org.vl4ds4m.banking.common.security;

public enum SecurityRole {

    ADMIN("admin"),
    ACCOUNTS_OPERATOR("accounts-operator"),
    CONVERTER_USER("converter-user"),
    RATES_USER("rates-user");

    public static final String PREFIX = "ROLE_";

    private final String role;

    SecurityRole(String role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return role;
    }

    public String toAuthority() {
        return PREFIX + role;
    }

}
