package io.github.yvasyliev.forwarder.telegram.bot.configuration;

import io.github.yvasyliev.forwarder.telegram.core.dto.PartialBotApiMethodDTO;
import io.github.yvasyliev.forwarder.telegram.core.util.CloseableSupplier;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.integration.core.GenericHandler;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.messaging.MessageChannel;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

/**
 * Configuration class for setting up Telegram integration.
 */
@Configuration
public class TelegramIntegrationConfiguration {
    /**
     * Creates a single-threaded {@link TaskScheduler} for polling {@link #postSourceChannel}, so posts and their
     * controls are sent strictly one after another instead of being polled concurrently.
     *
     * @return a {@link TaskScheduler} instance
     */
    @Bean(defaultCandidate = false)
    public TaskScheduler postSendingTaskScheduler() {
        var taskScheduler = new ThreadPoolTaskScheduler();

        taskScheduler.setWaitForTasksToCompleteOnShutdown(true);
        taskScheduler.setThreadNamePrefix("post-sending-");

        return taskScheduler;
    }

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
     * @param postSourceChannel        the source channel for posts
     * @param postSendingHandler       the handler for sending posts
     * @param postSendingTaskScheduler the task scheduler for handling incoming posts
     * @return an {@link IntegrationFlow} instance
     */
    @Bean
    public IntegrationFlow postSendingIntegrationFlow(
            MessageChannel postSourceChannel,
            GenericHandler<CloseableSupplier<PartialBotApiMethodDTO>> postSendingHandler,
            @Qualifier("postSendingTaskScheduler") TaskScheduler postSendingTaskScheduler
    ) {
        return IntegrationFlow.from(postSourceChannel)
                .handle(postSendingHandler, e -> e.taskScheduler(postSendingTaskScheduler))
                .nullChannel();
    }
}
