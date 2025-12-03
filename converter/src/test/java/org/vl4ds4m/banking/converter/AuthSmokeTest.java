package org.vl4ds4m.banking.converter;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.vl4ds4m.banking.common.auth.Auth;

@SpringBootTest
@DirtiesContext
@ActiveProfiles(Auth.PROFILE)
class AuthSmokeTest {

    //@org.junit.jupiter.api.Test
    void contextLoads() {}

}
