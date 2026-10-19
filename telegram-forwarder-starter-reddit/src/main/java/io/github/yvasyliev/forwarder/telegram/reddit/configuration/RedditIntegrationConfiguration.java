package io.github.yvasyliev.forwarder.telegram.reddit.configuration;

import io.github.yvasyliev.forwarder.telegram.reddit.converter.RedditLinkConverterResolver;
import io.github.yvasyliev.forwarder.telegram.reddit.dto.Link;
import io.github.yvasyliev.forwarder.telegram.reddit.service.RedditLinkService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.PollerFactory;
import org.springframework.integration.handler.MessageProcessor;
import org.springframework.integration.jdbc.metadata.JdbcMetadataStore;
import org.springframework.integration.metadata.ConcurrentMetadataStore;
import org.springframework.integration.selector.MetadataStoreSelector;
import org.springframework.messaging.MessageChannel;

import javax.sql.DataSource;
import java.util.function.BiPredicate;

/**
 * Configuration for Reddit integration.
 */
@Configuration
@EnableIntegration
public class RedditIntegrationConfiguration {
    /**
     * Configures the metadata store for Reddit.
     *
     * @param dataSource the data source for the metadata store
     * @return the {@link ConcurrentMetadataStore} for Reddit
     */
    @Bean
    public ConcurrentMetadataStore redditMetadataStore(DataSource dataSource) {
        var jdbcMetadataStore = new JdbcMetadataStore(dataSource);

        jdbcMetadataStore.setRegion("reddit");

        return jdbcMetadataStore;
    }

    /**
     * Configures the metadata store selector for Reddit.
     *
     * @param redditMetadataStore the metadata store for Reddit
     * @return the {@link MetadataStoreSelector} for Reddit
     */
    @Bean
    public MetadataStoreSelector redditMetadataStoreSelector(ConcurrentMetadataStore redditMetadataStore) {
        var keyStrategy = (MessageProcessor<String>) message -> {
            var link = (Link) message.getPayload();

            return link.subreddit();
        };
        var valueStrategy = (MessageProcessor<String>) message -> {
            var link = (Link) message.getPayload();
            var createdAt = link.created().toEpochMilli();

            return String.valueOf(createdAt);
        };
        var compareValues = (BiPredicate<String, String>) (oldValue, newValue) ->
                Long.parseLong(newValue) > Long.parseLong(oldValue);

        return new MetadataStoreSelector(keyStrategy, valueStrategy, redditMetadataStore)
                .compareValues(compareValues);
    }

    /**
     * Configures the integration flow for Reddit.
     *
     * @param redditLinkService           the Reddit link service
     * @param redditPollerProperties      the Reddit poller properties
     * @param redditMetadataStoreSelector the metadata store selector for Reddit
     * @param redditLinkConverterResolver the Reddit link converter manager
     * @param postSourceChannel           the post source message channel
     * @return the {@link IntegrationFlow} for Reddit
     */
    @Bean
    public IntegrationFlow redditIntegrationFlow(
            RedditLinkService redditLinkService,
            RedditPollerProperties redditPollerProperties,
            MetadataStoreSelector redditMetadataStoreSelector,
            RedditLinkConverterResolver redditLinkConverterResolver,
            MessageChannel postSourceChannel
    ) {
        return IntegrationFlow.fromSupplier(
                        redditLinkService::getSubredditNew,
                        e -> e.poller(_ -> PollerFactory.fixedDelay(redditPollerProperties.fixedDelay()))
                )
                .split()
                .filter(redditMetadataStoreSelector)
                .transform(source -> {
                    var link = (Link) source;

                    return redditLinkConverterResolver.convert(link.sourceLink());
                })
                .split()
                .channel(postSourceChannel)
                .get();
    }
}
