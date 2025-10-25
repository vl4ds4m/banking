package org.vl4ds4m.banking.accounts.customer.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;

import java.time.LocalDate;

public record CustomerCreationRequest(
    @JsonProperty("firstName")
    @NotBlank
    String firstName,

    @JsonProperty("lastName")
    @NotBlank
    String lastName,

    @JsonProperty("birthDate")
    @JsonAlias("birthDay")
    @NotNull
    @Past
    LocalDate birthDate
) {
}
