package com.github.yvasyliev.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record PostApprovedData(
        @JsonProperty("a") String action,
        @JsonProperty("fci") String fromChatId,
        @JsonProperty("mi") int messageId
) {
}
