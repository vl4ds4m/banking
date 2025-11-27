package org.vl4ds4m.banking.accounts;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.vl4ds4m.banking.common.Common;

@SpringBootApplication(scanBasePackageClasses = {Main.class, Common.class})
@EnableCaching
@EnableAspectJAutoProxy
public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
}
