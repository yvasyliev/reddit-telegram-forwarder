package com.github.yvasyliev.bots.telegram;

import org.springframework.beans.factory.annotation.Value;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

public abstract class AbstractRedTelBot extends TelegramLongPollingBot {
    @Value("${BOT_USERNAME}")
    private String botUsername;

    @Value("${telegram.admin.id}")
    private String adminId;

    @Value("${telegram.channel.id}")
    private String channelId;

    @Value("${GROUP_ID}")
    private String groupId;

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
}
