package com.github.yvasyliev.service.telegram.commands;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.net.URISyntaxException;

@Service("/cancel")
public class Cancel extends Command {
    @Override
    public void execute(Message message) throws TelegramApiException, URISyntaxException, IOException {
        redditTelegramForwarderBot.removeUserCommand(message.getFrom().getId());
        reply(message, "responses/cancel.md");
    }
}
