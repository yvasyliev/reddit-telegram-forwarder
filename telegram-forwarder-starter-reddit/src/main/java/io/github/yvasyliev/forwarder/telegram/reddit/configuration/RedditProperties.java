package io.github.yvasyliev.forwarder.telegram.reddit.configuration;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.net.URI;

/**
 * Reddit configuration properties.
 *
 * @param host      Reddit host
 * @param subreddit subreddit name
 * @param username  developer username
 * @param userAgent User-Agent HTTP header
 */
@ConfigurationProperties("reddit")
@Validated
@Builder
public record RedditProperties(
        @NotNull URI host,
        @NotBlank String subreddit,
        @NotBlank String username,
        @NotBlank String userAgent
) {}
