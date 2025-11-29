package org.vl4ds4m.banking.common;

import org.springframework.core.env.Environment;

/**
 * Root class of common library for component scan in Spring applications
 */
public final class Common {

    private static final String PROFILE_STANDALONE = "standalone";

    private Common() {
        throw new AssertionError();
    }

    public static boolean isRunStandalone(Environment environment) {
        return environment.matchesProfiles(PROFILE_STANDALONE);
    }
}
