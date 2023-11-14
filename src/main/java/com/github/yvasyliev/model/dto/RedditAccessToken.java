package com.github.yvasyliev.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.github.yvasyliev.service.deserializers.ExpiresAtDeserializer;

import java.time.LocalDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
public record RedditAccessToken(@JsonProperty("access_token") String token,
                                @JsonProperty("expires_in") @JsonDeserialize(using = ExpiresAtDeserializer.class) LocalDateTime expiresAt) {
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }
}
