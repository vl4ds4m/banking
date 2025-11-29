package org.vl4ds4m.banking.common.handler.idempotency;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.http.MediaType;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.ContentCachingResponseWrapper;
import org.vl4ds4m.banking.common.exception.InvalidQueryException;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class IdempotencyInterceptor implements HandlerInterceptor {

    private static final String IDEMPOTENCY_HEADER = "Idempotency-Key";

    private final IdempotencyHandler idempotencyHandler;

    @Override
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler
    ) throws IOException {
        if (skipInterceptor(handler)) {
            return true;
        }

        String idempotencyKey = getIdempotencyKey(request);
        var idempotencyValue = idempotencyHandler.preHandle(idempotencyKey);
        if (idempotencyValue == null) {
            return true;
        }

        response.setStatus(idempotencyValue.status());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(idempotencyValue.content());
        return false;
    }

    @Override
    public void postHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler,
            @Nullable ModelAndView modelAndView
    ) throws IOException {
        if (skipInterceptor(handler)) {
            return;
        }

        var responseWrapper = (ContentCachingResponseWrapper) response;
        var content = new String(responseWrapper.getContentAsByteArray(), response.getCharacterEncoding());
        var idempotencyValue = new IdempotencyValue(response.getStatus(), content);

        String idempotencyKey = getIdempotencyKey(request);
        idempotencyHandler.postHandle(idempotencyKey, idempotencyValue);
    }

    private static boolean skipInterceptor(Object handler) {
        if (handler instanceof HandlerMethod handlerMethod) {
            return !hasIdempotentAnnotation(handlerMethod);
        }
        return true;
    }

    private static boolean hasIdempotentAnnotation(HandlerMethod handlerMethod) {
        if (handlerMethod.hasMethodAnnotation(Idempotent.class)) {
            return true;
        }
        return handlerMethod.getBeanType().getAnnotation(Idempotent.class) != null;
    }

    private String getIdempotencyKey(HttpServletRequest request) {
        String key = request.getHeader(IDEMPOTENCY_HEADER);
        if (!idempotencyHandler.isIdempotencyKeyValid(key)) {
            throw new InvalidQueryException("Idempotency key '" + key + "' is invalid");
        }
        return key;
    }
}