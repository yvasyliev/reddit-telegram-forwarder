package io.github.yvasyliev.forwarder.telegram.reddit.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;

/**
 * Configuration properties for the Reddit poller.
 *
 * @param fixedDelay the fixed delay between poller executions, default is 1 minute
 */
@ConfigurationProperties("spring.integration.reddit.poller")
@Validated
public record RedditPollerProperties(@DefaultValue("1m") Duration fixedDelay) {}
