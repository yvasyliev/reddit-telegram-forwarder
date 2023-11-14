package com.github.yvasyliev.service.telegram.commands;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.net.URISyntaxException;

@Service("/stop")
public class Stop extends AdminCommand {
    @Autowired
    private ConfigurableApplicationContext context;

    @Override
    protected void execute(Message message) throws TelegramApiException, URISyntaxException, IOException {
        context.close();
        reply(message, "responses/stop.md", redditTelegramForwarderBot.getBotUsername());
    }
}
