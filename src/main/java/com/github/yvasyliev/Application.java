package com.github.yvasyliev;

import com.github.yvasyliev.service.telegram.commands.Start;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.ApplicationPidFileWriter;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.telegram.telegrambots.starter.TelegramBotStarterConfiguration;

@SpringBootApplication
public class Application extends TelegramBotStarterConfiguration {
    private static ConfigurableApplicationContext context;

    public static void main(String[] args) {
        context = new SpringApplicationBuilder(Application.class)
                .listeners(new ApplicationPidFileWriter())
                .build()
                .run(args);
    }

    public static ConfigurableApplicationContext getContext() {
        return context;
    }

    @Bean("/help")
    public Start help(Start start) {
        return start;
    }
}
