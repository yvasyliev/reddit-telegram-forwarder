package com.github.yvasyliev.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Configuration
@ComponentScan("com.github.yvasyliev.bots.telegram.notifier")
public class TelegramNotifierConfig {
    @Bean
    public Executor delayedExecutor() {
        return CompletableFuture.delayedExecutor(10, TimeUnit.SECONDS, singleThreadExecutor());
    }

    @Bean
    public Executor singleThreadExecutor() {
        return Executors.newSingleThreadExecutor();
    }
}
