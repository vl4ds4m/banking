package edu.vl4ds4m.banking.idempotency;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.*;
import org.springframework.lang.NonNull;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.time.Duration;
import java.util.Optional;

public class IdempotencyFilter extends OncePerRequestFilter {
    public record IdempotencyValue(int status, String body, boolean isDone) {
        static IdempotencyValue inProgress() {
            return new IdempotencyValue(0, "", false);
        }
    }

    private static final String IDEMPOTENCY_KEY = "Idempotency-Key";
    private static final String BLANK = "";
    private static final String DELIMITER = "_";

    private static final Logger logger = LoggerFactory.getLogger(IdempotencyFilter.class);

    private final RedisTemplate<String, IdempotencyValue> redisTemplate;
    private final Duration ttl;

    public IdempotencyFilter(RedisTemplate<String, IdempotencyValue> redisTemplate, Duration ttl) {
        this.redisTemplate = redisTemplate;
        this.ttl = ttl;
    }

    @Override
    protected void doFilterInternal(
        @NonNull HttpServletRequest request,
        @NonNull HttpServletResponse response,
        @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        String method = request.getMethod();
        String idempotencyKey = Optional.ofNullable(request.getHeader(IDEMPOTENCY_KEY))
            .orElse(BLANK);
        if (!isTargetMethod(method) || idempotencyKey.isBlank()) {
            filterChain.doFilter(request, response);
            return;
        }

        String cacheKey = String.join(DELIMITER,
            method, request.getRequestURI(), idempotencyKey);
        BoundValueOperations<String, IdempotencyValue> valueOps = redisTemplate.boundValueOps(cacheKey);
        boolean isAbsent = valueOps.setIfAbsent(IdempotencyValue.inProgress(), ttl);
        if (isAbsent) {
            ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);

            filterChain.doFilter(request, responseWrapper);

            setResultInCache(request, responseWrapper, valueOps);
            responseWrapper.copyBodyToResponse();
        } else {
            IdempotencyValue cachedResponse = valueOps.get();
            if (cachedResponse.isDone) {
                logger.debug("Return a cached response [{} {}]",
                    request.getMethod(), request.getRequestURI());

                response.setStatus(cachedResponse.status);

                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                response.getWriter().write(cachedResponse.body);
            } else {
                logger.warn("Response for given idempotency key is still in progress");

                response.setStatus(HttpStatus.TOO_EARLY.value());
            }
            response.flushBuffer();
        }
    }

    private boolean isTargetMethod(String method) {
        return HttpMethod.POST.matches(method);
    }

    private void setResultInCache(
        HttpServletRequest request,
        ContentCachingResponseWrapper responseWrapper,
        BoundValueOperations<String, IdempotencyValue> valueOps
    ) throws UnsupportedEncodingException {
        if (!needCache(responseWrapper)) {
            valueOps.getAndDelete();
            return;
        }

        logger.debug("Persist a response in cache [{} {}]",
            request.getMethod(), request.getRequestURI());

        String responseBody = new String(
            responseWrapper.getContentAsByteArray(),
            request.getCharacterEncoding());
        IdempotencyValue result = new IdempotencyValue(
            responseWrapper.getStatus(),
            responseBody, true);
        valueOps.set(result, ttl);
    }

    private boolean needCache(ContentCachingResponseWrapper responseWrapper) {
        int statusCode = responseWrapper.getStatus();
        return statusCode >= 200 && statusCode < 300;
    }
}