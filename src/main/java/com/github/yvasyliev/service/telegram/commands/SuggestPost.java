package com.github.yvasyliev.service.telegram.commands;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.net.URISyntaxException;

@Service("/suggestpost")
public class SuggestPost extends Command {
    @Override
    protected void execute(Message message) throws TelegramApiException, URISyntaxException, IOException {
        redditTelegramForwarderBot.addUserCommand(message.getFrom().getId(), "/postsuggested");
        reply(message, "responses/suggestpost.md");
    }
}
