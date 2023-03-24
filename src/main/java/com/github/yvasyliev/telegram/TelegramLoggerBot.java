package com.github.yvasyliev.telegram;

import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@FunctionalInterface
public interface TelegramLoggerBot {
    void log(String text) throws TelegramApiException;
}
