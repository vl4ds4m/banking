package edu.vl4ds4m.banking;

import edu.vl4ds4m.banking.auth.Auth;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@DirtiesContext
@ActiveProfiles(Auth.PROFILE)
class AuthSmokeTest {
    @Test
    void contextLoads() {
    }
}
