package org.vl4ds4m.banking.converter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.vl4ds4m.banking.common.Common;

@SpringBootApplication(
        scanBasePackageClasses = {Main.class, Common.class})
public class Main {

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
}
