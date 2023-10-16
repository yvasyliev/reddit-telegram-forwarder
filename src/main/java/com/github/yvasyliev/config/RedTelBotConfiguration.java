package com.github.yvasyliev.config;

import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.github.yvasyliev.bots.telegram.RedTelBot;
import com.github.yvasyliev.model.dto.Post;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.io.File;
import java.net.http.HttpClient;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Configuration
@ComponentScan("com.github.yvasyliev.service")
public class RedTelBotConfiguration extends TelegramNotifierConfig {
    @Autowired
    private JsonDeserializer<Post> postJsonDeserializer;

    @Bean
    public TelegramBotsApi telegramBotsApi() throws TelegramApiException {
        return new TelegramBotsApi(DefaultBotSession.class);
    }

    @Bean
    public ObjectMapper objectMapper() {
        var mapper = new ObjectMapper();
        mapper.registerModule(module());
        return mapper;
    }

    @Bean
    public Module module() {
        var module = new SimpleModule();
        module.addDeserializer(Post.class, postJsonDeserializer);
        return module;
    }

    @Bean
    public ScheduledExecutorService scheduledExecutorService() {
        return Executors.newSingleThreadScheduledExecutor();
    }

    @Bean
    public String userAgent(@Value("${REDDIT_USERNAME}") String redditUsername) {
        return "java:reddit-telegram-repeater:2.0.0  (by /u/%s)".formatted(redditUsername);
    }

    @Bean
    public RedTelBot redTelBot(@Value("${BOT_TOKEN}") String botToken) {
        return new RedTelBot(botToken);
    }

    @Bean
    public HttpClient httpClient() {
        return HttpClient.newHttpClient();
    }

    @Bean
    public File stateSrc() {
        return new File("state.json");
    }
}
