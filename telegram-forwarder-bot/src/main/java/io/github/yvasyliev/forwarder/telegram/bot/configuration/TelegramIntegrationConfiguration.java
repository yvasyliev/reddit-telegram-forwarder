package io.github.yvasyliev.forwarder.telegram.bot.configuration;

import io.github.yvasyliev.forwarder.telegram.core.dto.PartialBotApiMethodDTO;
import io.github.yvasyliev.forwarder.telegram.core.util.CloseableSupplier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.integration.core.GenericHandler;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.messaging.MessageChannel;

/**
 * Configuration class for setting up Telegram integration.
 */
@Configuration
public class TelegramIntegrationConfiguration {
    /**
     * Creates a {@link MessageChannel} bean for the post source channel with a specified capacity.
     *
     * @param telegramIntegrationProperties the properties for Telegram integration
     * @return a {@link MessageChannel} instance
     */
    @Bean
    public MessageChannel postSourceChannel(TelegramIntegrationProperties telegramIntegrationProperties) {
        return new QueueChannel(telegramIntegrationProperties.postSourceChannelCapacity());
    }

    /**
     * Creates an {@link IntegrationFlow} bean for handling post sending.
     *
     * @param postSourceChannel  the source channel for posts
     * @param postSendingHandler the handler for sending posts
     * @return an {@link IntegrationFlow} instance
     */
    @Bean
    public IntegrationFlow postSendingIntegrationFlow(
            MessageChannel postSourceChannel,
            GenericHandler<CloseableSupplier<PartialBotApiMethodDTO>> postSendingHandler
    ) {
        return IntegrationFlow.from(postSourceChannel).handle(postSendingHandler).nullChannel();
    }
}
