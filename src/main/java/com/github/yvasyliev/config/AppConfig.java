package com.github.yvasyliev.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.yvasyliev.dto.RedditAccessToken;
import com.github.yvasyliev.factories.RedditAccessTokenFactory;
import com.github.yvasyliev.properties.AppData;
import com.github.yvasyliev.service.reddit.RedditPostService;
import com.github.yvasyliev.service.reddit.RedditVideoDownloader;
import com.github.yvasyliev.service.reddit.api.GetRedditAccessToken;
import com.github.yvasyliev.service.reddit.api.GetSubredditNew;
import com.github.yvasyliev.service.reddit.api.Request;
import com.github.yvasyliev.telegram.TelegramRepeaterBot;
import com.github.yvasyliev.telegram.TelegramRepeaterBotImpl;
import com.github.yvasyliev.telegram.TelegramSenderBot;
import com.github.yvasyliev.telegram.TelegramSenderBotImpl;
import com.github.yvasyliev.telegram.chain.FilterAuthorChain;
import com.github.yvasyliev.telegram.chain.NOPRepeaterChain;
import com.github.yvasyliev.telegram.chain.RepeatGif;
import com.github.yvasyliev.telegram.chain.RepeatMultiplePhotos;
import com.github.yvasyliev.telegram.chain.RepeatNestedPost;
import com.github.yvasyliev.telegram.chain.RepeatPhoto;
import com.github.yvasyliev.telegram.chain.RepeatPoll;
import com.github.yvasyliev.telegram.chain.RepeatText;
import com.github.yvasyliev.telegram.chain.RepeatVideo;
import com.github.yvasyliev.telegram.chain.SubredditPostRepeaterChain;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import java.net.http.HttpClient;
import java.util.Properties;

@Configuration
public class AppConfig extends TelegramLoggerBotConfig {
    @Bean(initMethod = "load", destroyMethod = "store")
    public Properties appData() {
        return new AppData();
    }

    @Bean
    public TelegramRepeaterBot telegramRepeaterBot(@Value("${BOT_TOKEN}") String botToken) {
        return new TelegramRepeaterBotImpl(botToken);
    }

    @Bean
    public TelegramSenderBot telegramSenderBot(@Value("${BOT_TOKEN}") String botToken) {
        return new TelegramSenderBotImpl(botToken);
    }

    @Bean
    public FactoryBean<RedditAccessToken> redditAccessTokenFactory() {
        return new RedditAccessTokenFactory();
    }

    @Bean
    @Scope(BeanDefinition.SCOPE_PROTOTYPE)
    public RedditAccessToken redditAccessToken() throws Exception {
        return redditAccessTokenFactory().getObject();
    }

    @Bean(initMethod = "initializeRequest")
    public Request<RedditAccessToken> getRedditAccessToken() {
        return new GetRedditAccessToken();
    }

    @Bean
    public String userAgent(@Value("${REDDIT_USERNAME}") String redditUsername) {
        return "java:reddit-telegram-repeater:2.0.0  (by /u/%s)".formatted(redditUsername);
    }

    @Bean
    public HttpClient httpClient() {
        return HttpClient.newHttpClient();
    }

    @Bean
    public Request<JsonNode> getSubredditNew() {
        return new GetSubredditNew();
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Bean
    public RedditPostService redditPostService() {
        return new RedditPostService();
    }

    @Bean
    public SubredditPostRepeaterChain repeatNestedPost() {
        return new RepeatNestedPost(repeatText());
    }

    @Bean
    public SubredditPostRepeaterChain repeatText() {
        return new RepeatText(repeatPhoto());
    }

    @Bean
    public SubredditPostRepeaterChain repeatPhoto() {
        return new RepeatPhoto(repeatMultiplePhotos());
    }

    @Bean
    public SubredditPostRepeaterChain repeatMultiplePhotos() {
        return new RepeatMultiplePhotos(repeatGif());
    }

    @Bean
    public SubredditPostRepeaterChain repeatGif() {
        return new RepeatGif(repeatVideo());
    }

    @Bean
    public SubredditPostRepeaterChain repeatVideo() {
        return new RepeatVideo(repeatPoll());
    }

    @Bean
    public RedditVideoDownloader redditVideoDownloader() {
        return new RedditVideoDownloader();
    }

    @Bean
    public SubredditPostRepeaterChain nopRepeaterChain() {
        return new NOPRepeaterChain();
    }

    @Bean
    public SubredditPostRepeaterChain repeatPoll() {
        return new RepeatPoll(nopRepeaterChain());
    }

    @Bean(name = {"filterAuthorChain", "subredditPostRepeaterChain"})
    public SubredditPostRepeaterChain filterAuthorChain() {
        return new FilterAuthorChain(repeatNestedPost());
    }
}