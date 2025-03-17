package edu.vl4ds4m.banking.customer.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record CustomerCreationRequest(
    @JsonProperty("firstName")
    @NotBlank
    String firstName,

    @JsonProperty("lastName")
    @NotBlank
    String lastName,

    @JsonProperty("birthDate")
    @NotNull
    @Past
    LocalDate birthDate
) {
}
