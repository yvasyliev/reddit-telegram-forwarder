package com.github.yvasyliev;

import com.github.yvasyliev.config.AppConfig;
import com.github.yvasyliev.telegram.AnadeArmasFanbot;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

public class Main {
    public static void main(String[] args) {
        AbstractApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        context.registerShutdownHook();

        AnadeArmasFanbot anadeArmasFanbot = context.getBean(AnadeArmasFanbot.class);
        anadeArmasFanbot.processRedditPosts();
    }
}
