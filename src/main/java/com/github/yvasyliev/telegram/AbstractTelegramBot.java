package com.github.yvasyliev.telegram;

import org.springframework.beans.factory.annotation.Value;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

public abstract class AbstractTelegramBot extends TelegramLongPollingBot {
    @Value("${BOT_USERNAME}")
    private String botUsername;

    public AbstractTelegramBot(String botToken) {
        super(botToken);
    }

    @Override
    public void onUpdateReceived(Update update) {
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }
}
