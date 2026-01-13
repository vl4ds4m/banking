package org.vl4ds4m.banking.accounts;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.vl4ds4m.banking.common.Common;

@SpringBootApplication(scanBasePackageClasses = {App.class, Common.class})
// TODO repair rate limiting and
//  use @EnableCaching and @EnableAspectJAutoProxy
public class App {

    public static final String OAUTH2_CLIENT_REG = "accounts-client-reg";

    static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

}
