package edu.vl4ds4m.banking.admin.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record UpdateConfigsRequest(
    @JsonProperty("fee")
    @NotNull
    @Min(0) @Max(1)
    BigDecimal fee
) {
}
