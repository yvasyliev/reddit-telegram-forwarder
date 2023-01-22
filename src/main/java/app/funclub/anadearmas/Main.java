package app.funclub.anadearmas;

import app.funclub.anadearmas.config.AppConfig;
import app.funclub.anadearmas.telegram.AnadeArmasFanbot;
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
