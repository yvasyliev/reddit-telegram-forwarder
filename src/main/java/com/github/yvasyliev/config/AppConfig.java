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
import com.github.yvasyliev.telegram.NOPRepeaterChain;
import com.github.yvasyliev.telegram.RepeatGif;
import com.github.yvasyliev.telegram.RepeatMultiplePhotos;
import com.github.yvasyliev.telegram.RepeatNestedPost;
import com.github.yvasyliev.telegram.RepeatPhoto;
import com.github.yvasyliev.telegram.RepeatText;
import com.github.yvasyliev.telegram.RepeatVideo;
import com.github.yvasyliev.telegram.SubredditPostRepeaterChain;
import com.github.yvasyliev.telegram.TelegramRepeaterBot;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import java.net.http.HttpClient;
import java.util.Properties;

@Configuration
public class AppConfig {
    @Bean(initMethod = "load", destroyMethod = "store")
    public Properties appData() {
        return new AppData();
    }

    @Bean
    public TelegramRepeaterBot telegramRepeaterBot(@Value("${BOT_TOKEN}") String botToken) {
        return new TelegramRepeaterBot(botToken);
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
        return "java:reddit-telegram-repeater:2.0.0  (by /u/" + redditUsername + ")";
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

    @Bean(name = {"repeatNestedPost", "subredditPostRepeaterChain"})
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
        return new RepeatVideo(nopRepeaterChain());
    }

    @Bean
    public RedditVideoDownloader redditVideoDownloader() {
        return new RedditVideoDownloader();
    }

    @Bean
    public SubredditPostRepeaterChain nopRepeaterChain() {
        return new NOPRepeaterChain();
    }
}