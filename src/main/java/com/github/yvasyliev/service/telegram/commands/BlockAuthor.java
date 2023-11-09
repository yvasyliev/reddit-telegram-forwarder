package com.github.yvasyliev.service.telegram.commands;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.net.URISyntaxException;

@Service("/blockauthor")
public class BlockAuthor extends AdminCommand {
    @Override
    protected void execute(Message message) throws URISyntaxException, IOException, TelegramApiException {
        redditTelegramForwarderBot.addUserCommand(message.getFrom().getId(), "/addblockedauthor");
        reply(message, "responses/blockauthor.md");
    }
}
