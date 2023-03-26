package com.github.yvasyliev.config;

import com.github.yvasyliev.telegram.TelegramLoggerBot;
import com.github.yvasyliev.telegram.TelegramLoggerBotImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TelegramLoggerBotConfig {
    @Bean
    public TelegramLoggerBot telegramLoggerBot(@Value("${BOT_TOKEN}") String botToken) {
        return new TelegramLoggerBotImpl(botToken);
    }
}
