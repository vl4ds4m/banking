package org.vl4ds4m.banking.rates;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.vl4ds4m.banking.common.Common;

@SpringBootApplication(scanBasePackageClasses = {App.class, Common.class})
public class App {

    static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

}
