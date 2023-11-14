package com.github.yvasyliev.service.telegram.commands;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.net.URISyntaxException;

@Service("/done")
public class Done extends Cancel {
    @Override
    protected Message reply(Message to, String template, Object... args) throws URISyntaxException, IOException, TelegramApiException {
        return super.reply(to, "responses/done.md");
    }
}
