package com.github.yvasyliev;

import com.github.yvasyliev.bots.telegram.RedTelBot;
import com.github.yvasyliev.config.RedTelBotConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class Application {
    private static final Logger LOGGER = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) throws TelegramApiException {
        var context = new AnnotationConfigApplicationContext(RedTelBotConfiguration.class);
        context.registerShutdownHook();

        var redTelBot = context.getBean(RedTelBot.class);
        redTelBot.startPolling();
        redTelBot.startPublishingPosts();
    }
}
