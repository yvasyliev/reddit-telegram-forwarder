package io.github.yvasyliev.forwarder.telegram.reddit.configuration;

import org.apache.commons.lang3.BooleanUtils;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.resilience.annotation.EnableResilientMethods;

/**
 * Auto-configuration for Reddit integration.
 */
@AutoConfiguration
@ConditionalOnProperty(prefix = "reddit", name = "enabled", havingValue = BooleanUtils.TRUE, matchIfMissing = true)
@EnableConfigurationProperties({
        RedditPollerProperties.class,
        RedditProperties.class,
        RedditVideoDownloaderProperties.class
})
@EnableResilientMethods
@ComponentScan({
        "io.github.yvasyliev.forwarder.telegram.reddit.converter",
        "io.github.yvasyliev.forwarder.telegram.reddit.mapper",
        "io.github.yvasyliev.forwarder.telegram.reddit.service",
        "io.github.yvasyliev.forwarder.telegram.reddit.util"
})
@Import({RedditClientConfiguration.class, RedditIntegrationConfiguration.class})
public class RedditAutoConfiguration {}
