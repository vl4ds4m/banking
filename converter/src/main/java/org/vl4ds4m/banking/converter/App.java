package org.vl4ds4m.banking.converter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.vl4ds4m.banking.common.Common;

@SpringBootApplication(scanBasePackageClasses = {App.class, Common.class})
public class App {

    public static final String OAUTH2_CLIENT_REG = "converter-client-reg";

    static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

}
