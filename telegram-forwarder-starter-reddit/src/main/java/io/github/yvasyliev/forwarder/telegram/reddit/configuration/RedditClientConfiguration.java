package io.github.yvasyliev.forwarder.telegram.reddit.configuration;

import io.github.yvasyliev.forwarder.telegram.reddit.service.RedditClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.AuthorizedClientServiceOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.client.support.OAuth2RestClientHttpServiceGroupConfigurer;
import org.springframework.web.client.support.RestClientHttpServiceGroupConfigurer;
import org.springframework.web.service.registry.ImportHttpServices;

/**
 * Configuration for {@link RedditClient}.
 */
@Configuration
@ImportHttpServices(group = RedditClient.REDDIT_GROUP, types = RedditClient.class)
public class RedditClientConfiguration {
    /**
     * Configures the OAuth2 Rest Client for Reddit.
     *
     * @param clientRegistrationRepository the client registration repository
     * @param authorizedClientService      the authorized client service
     * @return the {@link RestClientHttpServiceGroupConfigurer} for Reddit
     */
    @Bean
    public RestClientHttpServiceGroupConfigurer redditOAuth2RestClientHttpServiceGroupConfigurer(
            ClientRegistrationRepository clientRegistrationRepository,
            OAuth2AuthorizedClientService authorizedClientService
    ) {
        var authorizedClientManager = new AuthorizedClientServiceOAuth2AuthorizedClientManager(
                clientRegistrationRepository,
                authorizedClientService
        );

        return OAuth2RestClientHttpServiceGroupConfigurer.from(authorizedClientManager);
    }
}
