package com.github.yvasyliev.service.telegram.commands;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Optional;
import java.util.function.Function;

@Service("/start")
public class Start extends Command {
    @Autowired
    private Chat channelChat;

    @Autowired
    private Function<String, String> markdownV2Escaper;

    @Override
    protected void execute(Message message) throws TelegramApiException, URISyntaxException, IOException {
        var channelId = Optional
                .ofNullable(channelChat.getUserName())
                .map(channelUsername -> "@%s".formatted(markdownV2Escaper.apply(channelUsername)))
                .orElse(markdownV2Escaper.apply(channelChat.getTitle()));
        if (redditTelegramForwarderBot.isAdmin(message.getFrom())) {
            reply(
                    message,
                    "responses/admin/start.md",
                    channelId,
                    redditTelegramForwarderBot.getMe().getFirstName()
            );
        } else {
            reply(message, "responses/start.md", channelId);
        }
    }
}
