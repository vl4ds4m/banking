package org.vl4ds4m.banking.webui.service;

import org.springframework.web.client.RestClientResponseException;
import org.vl4ds4m.banking.common.openapi.model.ErrorMessageResponse;

public class QueryExceptionHandler {

    private QueryExceptionHandler() {}

    public static String handle(RestClientResponseException e) {
        if (e.getStatusCode().is4xxClientError()) {
            var response = e.getResponseBodyAs(ErrorMessageResponse.class);
            if (response != null) return response.getMessage();
        }
        throw e;
    }
}
