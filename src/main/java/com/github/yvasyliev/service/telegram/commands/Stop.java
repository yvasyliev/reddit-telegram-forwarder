package com.github.yvasyliev.service.telegram.commands;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.net.URISyntaxException;

@Service("/stop")
public class Stop extends Command {
    @Override
    public void acceptWithException(Message message) throws TelegramApiException, URISyntaxException, IOException {
        redTelBot.stopPublishing();
        redTelBot.stopPolling();
        reply(message, "responses/stop.md");
    }
}
