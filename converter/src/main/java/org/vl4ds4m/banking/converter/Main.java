package org.vl4ds4m.banking.converter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "org.vl4ds4m.banking")
public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
}
