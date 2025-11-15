package org.vl4ds4m.banking.common.handler.log;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;

@Slf4j
public class HttpClientLogInterceptor implements ClientHttpRequestInterceptor {

    @Override
    public ClientHttpResponse intercept(
            HttpRequest request,
            byte[] body,
            ClientHttpRequestExecution execution
    ) throws IOException {
        var method = request.getMethod();
        var uri = request.getURI();
        log.info("Send HTTP request {} {}", method, uri);

        var response = execution.execute(request, body);

        var status = HttpStatus.resolve(response.getStatusCode().value());
        log.info("Receive HTTP response {} {}, status = [{}]", method, uri, status);

        return response;
    }
}
