package org.vl4ds4m.banking.webui.api.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.io.Resource;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.resource.ResourceResolver;
import org.springframework.web.servlet.resource.ResourceResolverChain;

import java.util.List;

public class FaviconResolver implements ResourceResolver {

    public static final String FILENAME = "favicon";

    @Override
    @Nullable
    public Resource resolveResource(
            @Nullable HttpServletRequest request,
            String requestPath,
            List<? extends Resource> locations,
            ResourceResolverChain chain
    ) {
        int start = requestPath.lastIndexOf(FILENAME);
        if (start >= 0) {
            requestPath = requestPath.substring(0, start) + FILENAME + ".png";
        }
        return chain.resolveResource(request, requestPath, locations);
    }

    @Override
    @Nullable
    public String resolveUrlPath(
            String resourcePath,
            List<? extends Resource> locations,
            ResourceResolverChain chain
    ) {
        return chain.resolveUrlPath(resourcePath, locations);
    }
}
