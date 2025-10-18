package org.vl4ds4m.banking.admin.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Action(
    @JsonProperty("action")
    Type type
) {
    public enum Type {
        UPDATE_FEE
    }
}
