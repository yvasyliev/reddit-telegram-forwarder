package com.github.yvasyliev;

import com.github.yvasyliev.config.AppConfig;
import com.github.yvasyliev.service.reddit.RedditPostService;
import com.github.yvasyliev.telegram.TelegramRepeaterBot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.io.IOException;

public class Application {
    private static final Logger LOGGER = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
        var context = new AnnotationConfigApplicationContext(AppConfig.class);
        context.registerShutdownHook();

        try {
            var redditPostService = context.getBean(RedditPostService.class);
            var newPosts = redditPostService.findNewPosts();

            var telegramRepeaterBot = context.getBean(TelegramRepeaterBot.class);
            telegramRepeaterBot.repeatSubredditPosts(newPosts);
        } catch (IOException | InterruptedException e) {
            LOGGER.error("Failed to find new Reddit posts.", e);
        }
    }
}
