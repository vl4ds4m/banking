package edu.vl4ds4m.tbank.dto;

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

        @JsonProperty("birthDay")
        @NotNull
        @Past
        LocalDate birthDate
) {
}
