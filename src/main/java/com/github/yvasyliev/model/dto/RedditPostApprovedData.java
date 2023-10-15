package com.github.yvasyliev.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record RedditPostApprovedData(
        @JsonProperty("a") String action,
        @JsonProperty("c") int created
) {
}
