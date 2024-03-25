package edu.tinkoff.config;

import edu.tinkoff.auth.AuthValidator;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    private final AuthValidator authValidator;

    public WebConfig(AuthValidator authValidator) {
        this.authValidator = authValidator;
    }

    @Override
    public void addInterceptors(@NonNull InterceptorRegistry registry) {
        registry.addInterceptor(authValidator).addPathPatterns("/convert");
    }
}
