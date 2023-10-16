package com.github.yvasyliev.service.telegram.commands;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.net.URISyntaxException;

@Service("/start")
public class Start extends Command {
    @Override
    public void acceptWithException(Message message) throws TelegramApiException, URISyntaxException, IOException {
        reply(message, "responses/start.md");
    }
}
