package com.github.yvasyliev.service.telegram.commands;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.net.URISyntaxException;

@Service("/start")
public class Start extends Command {
    @Value("${telegram.channel.name}")
    private String telegramChannelName;

    @Override
    protected void execute(Message message) throws TelegramApiException, URISyntaxException, IOException {
        if (redditTelegramForwarderBot.isAdmin(message.getFrom())) {
            reply(
                    message,
                    "responses/admin/start.md",
                    telegramChannelName,
                    redditTelegramForwarderBot.getMe().getFirstName()
            );
        } else {
            reply(message, "responses/start.md", telegramChannelName);
        }
    }
}
