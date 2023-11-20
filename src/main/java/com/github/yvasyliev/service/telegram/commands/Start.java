package com.github.yvasyliev.service.telegram.commands;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.function.Function;

@Service("/start")
public class Start extends Command {
    @Autowired
    private Chat channelChat;

    @Autowired
    private Function<String, String> markdownV2Escaper;

    @Value("${project.version}")
    private String projectVersion;

    @Override
    protected void execute(Message message) throws TelegramApiException, URISyntaxException, IOException {
        var botName = markdownV2Escaper.apply(redditTelegramForwarderBot.getMe().getFirstName());
        var botVersion = markdownV2Escaper.apply(projectVersion);
        var channelRef = getChannelRef();
        if (redditTelegramForwarderBot.isAdmin(message.getFrom())) {
            reply(
                    message,
                    "responses/admin/start.md",
                    botName,
                    botVersion,
                    channelRef,
                    botName
            );
        } else {
            reply(
                    message,
                    "responses/start.md",
                    botName,
                    botVersion,
                    channelRef
            );
        }
    }

    private String getChannelRef() {
        var userName = channelChat.getUserName();

        if (userName != null) {
            return "@%s".formatted(markdownV2Escaper.apply(userName));
        }

        return markdownV2Escaper.apply(channelChat.getTitle());
    }
}
