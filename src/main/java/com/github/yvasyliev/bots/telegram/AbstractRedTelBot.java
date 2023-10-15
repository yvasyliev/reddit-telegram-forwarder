package com.github.yvasyliev.bots.telegram;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMediaGroup;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;

public abstract class AbstractRedTelBot extends TelegramLongPollingBot {
    @Value("${BOT_USERNAME}")
    private String botUsername;

    @Value("${ADMIN_ID}")
    private String adminId;

    @Value("${CHANNEL_ID}")
    private String channelId;

    @Value("${GROUP_ID}")
    private String groupId;

    @Autowired
    private Executor delayedExecutor;

    public AbstractRedTelBot(String botToken) {
        super(botToken);
    }

    @Override
    public void onUpdateReceived(Update update) {
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    public String getAdminId() {
        return adminId;
    }

    public String getChannelId() {
        return channelId;
    }

    public String getGroupId() {
        return groupId;
    }

    public Executor getDelayedExecutor() {
        return delayedExecutor;
    }

    public List<Message> executeDelayed(SendMediaGroup sendMediaGroup) throws TelegramApiException {
        try {
            return CompletableFuture
                    .supplyAsync(() -> {
                                try {
                                    return execute(sendMediaGroup);
                                } catch (TelegramApiException e) {
                                    throw new CompletionException(e);
                                }
                            },
                            delayedExecutor
                    )
                    .get();
        } catch (InterruptedException | ExecutionException e) {
            throw new TelegramApiException(e);
        }
    }

    public Message executeDelayed(SendPhoto sendPhoto) throws TelegramApiException {
        try {
            return CompletableFuture
                    .supplyAsync(() -> {
                                try {
                                    return execute(sendPhoto);
                                } catch (TelegramApiException e) {
                                    throw new CompletionException(e);
                                }
                            },
                            delayedExecutor
                    )
                    .get();
        } catch (InterruptedException | ExecutionException e) {
            throw new TelegramApiException(e);
        }
    }
}
