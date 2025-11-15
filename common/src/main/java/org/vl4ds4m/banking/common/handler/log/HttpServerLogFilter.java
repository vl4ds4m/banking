package org.vl4ds4m.banking.common.handler.log;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Objects;

@Slf4j
public class HttpServerLogFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        var method = request.getMethod();
        var path = request.getRequestURI();
        var query = getQuery(request);
        log.info("Accept HTTP request {} {}{}", method, path, query);

        filterChain.doFilter(request, response);

        var status = Objects.requireNonNull(
                HttpStatus.resolve(response.getStatus()));
        var msg = "HTTP request {} {} processed, status = [{}]";

        if (status.is5xxServerError()) {
            log.warn(msg, method, path, status);
        } else {
            log.info(msg, method, path, status);
        }
    }

    private static String getQuery(HttpServletRequest request) {
        var q = request.getQueryString();
        if (q == null) return "";
        return "?" + q;
    }
}
