package com.github.yvasyliev;

import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.github.yvasyliev.model.dto.ExternalMessageData;
import com.github.yvasyliev.model.dto.post.PhotoGroupPost;
import com.github.yvasyliev.model.dto.post.Post;
import com.github.yvasyliev.service.telegram.commands.Start;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.ApplicationPidFileWriter;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.starter.TelegramBotStarterConfiguration;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.net.http.HttpClient;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;

@SpringBootApplication
@EnableScheduling
public class Application extends TelegramBotStarterConfiguration {
    private static ApplicationContext context;

    public static void main(String[] args) {
        context = new SpringApplicationBuilder(Application.class)
                .listeners(new ApplicationPidFileWriter())
                .build()
                .run(args);
    }

    public static void withContext(Consumer<ApplicationContext> contextConsumer) {
        if (context != null) {
            contextConsumer.accept(context);
        }
    }

    @Bean("/help")
    public Start help(Start start) {
        return start;
    }

    @Bean
    public TelegramBotsApi telegramBotsApi() throws TelegramApiException {
        return new TelegramBotsApi(DefaultBotSession.class);
    }

    @Bean
    public ObjectMapper objectMapper(JsonDeserializer<Post> postJsonDeserializer) {
        var mapper = new ObjectMapper();
        mapper.registerModule(module(postJsonDeserializer));
        return mapper;
    }

    @Bean
    public Module module(JsonDeserializer<Post> postJsonDeserializer) {
        var module = new SimpleModule();
        module.addDeserializer(Post.class, postJsonDeserializer);
        return module;
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
    @Scope(BeanDefinition.SCOPE_PROTOTYPE)
    public <K, V> Map<K, V> synchronizedFixedSizeMap(@Value("16") int maxSize) {
        return Collections.synchronizedMap(new LinkedHashMap<>(maxSize) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
                return size() >= maxSize;
            }
        });
    }

    @Bean
    @Scope(BeanDefinition.SCOPE_PROTOTYPE)
    public Map<Integer, Post> integerPostMap(@Value("16") int maxSize) {
        return synchronizedFixedSizeMap(maxSize);
    }

    @Bean
    @Scope(BeanDefinition.SCOPE_PROTOTYPE)
    public Map<Integer, PhotoGroupPost> integerPhotoGroupPostMap(@Value("16") int maxSize) {
        return synchronizedFixedSizeMap(maxSize);
    }

    @Bean
    @Scope(BeanDefinition.SCOPE_PROTOTYPE)
    public Map<Long, String> longStringMap(@Value("16") int maxSize) {
        return synchronizedFixedSizeMap(maxSize);
    }

    @Bean
    @Scope(BeanDefinition.SCOPE_PROTOTYPE)
    public Map<Long, ExternalMessageData> longExternalMessageDataMap(@Value("16") int maxSize) {
        return synchronizedFixedSizeMap(maxSize);
    }

}
