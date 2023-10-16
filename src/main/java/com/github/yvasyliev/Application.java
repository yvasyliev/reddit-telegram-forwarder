package com.github.yvasyliev;

import com.github.yvasyliev.bots.telegram.RedTelBot;
import com.github.yvasyliev.config.RedTelBotConfiguration;
import com.github.yvasyliev.service.telegram.PostManager;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class Application {
    public static void main(String[] args) throws TelegramApiException {
        var context = new AnnotationConfigApplicationContext(RedTelBotConfiguration.class);
        context.registerShutdownHook();

        var redTelBot = context.getBean(RedTelBot.class);
        redTelBot.startPolling();

        var postManager = context.getBean(PostManager.class);
        postManager.schedulePosting();
    }
}
