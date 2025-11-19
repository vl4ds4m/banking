package org.vl4ds4m.banking.webui.api.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final WebProperties webProperties;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/" + FaviconResolver.FILENAME + "*")
                .addResourceLocations(webProperties.getResources().getStaticLocations())
                .resourceChain(webProperties.getResources().getChain().isCache())
                .addResolver(new FaviconResolver());
    }
}
