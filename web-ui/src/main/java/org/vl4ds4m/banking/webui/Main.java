package org.vl4ds4m.banking.webui;

import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.vl4ds4m.banking.common.Common;

@SpringBootApplication(
        scanBasePackageClasses = {Main.class, Common.class})
@EnableConfigurationProperties({WebProperties.class})
public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    @Bean
    public ApplicationRunner applicationRunner() {
        return args -> {
            System.out.println("Application runner");
        };
    }
}
