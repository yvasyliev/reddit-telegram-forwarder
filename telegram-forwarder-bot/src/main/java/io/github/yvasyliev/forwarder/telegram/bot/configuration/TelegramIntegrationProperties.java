package io.github.yvasyliev.forwarder.telegram.bot.configuration;

import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.validation.annotation.Validated;

/**
 * Configuration properties for Telegram integration.
 *
 * @param postSourceChannelCapacity the capacity of the post source channel
 */
@ConfigurationProperties("spring.integration.telegram")
@Validated
public record TelegramIntegrationProperties(@DefaultValue("100") @NotNull Integer postSourceChannelCapacity) {}
