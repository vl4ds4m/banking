package org.vl4ds4m.banking.converter.api;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.vl4ds4m.banking.converter.service.exception.RatesServiceException;

@RequiredArgsConstructor
public class ConverterExceptionHandler {

    private final Logger log;

    public String warnRatesServiceError(RatesServiceException e) {
        log.warn("Handle RatesServiceException: {}", e.getMessage());
        return "Rates service is unavailable";
    }
}
