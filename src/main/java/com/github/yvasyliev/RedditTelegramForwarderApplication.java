package com.github.yvasyliev;

import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.github.yvasyliev.model.dto.post.Post;
import com.github.yvasyliev.service.telegram.commands.Start;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.ApplicationPidFileWriter;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.telegram.telegrambots.starter.TelegramBotStarterConfiguration;

import java.net.http.HttpClient;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;

@SpringBootApplication
@EnableScheduling
@Import(TelegramBotStarterConfiguration.class)
public class RedditTelegramForwarderApplication {
    private static ApplicationContext context;

    public static void main(String[] args) {
        context = new SpringApplicationBuilder(RedditTelegramForwarderApplication.class)
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
    public ObjectMapper objectMapper(JsonDeserializer<Post> postJsonDeserializer) {
        var module = new SimpleModule();
        module.addDeserializer(Post.class, postJsonDeserializer);

        var mapper = new ObjectMapper();
        mapper.registerModule(module);
        return mapper;
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
}
