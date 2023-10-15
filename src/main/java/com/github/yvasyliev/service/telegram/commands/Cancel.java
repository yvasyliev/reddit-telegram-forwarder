package com.github.yvasyliev.service.telegram.commands;

import com.github.yvasyliev.service.dao.UserCommandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.net.URISyntaxException;

@Service("/cancel")
public class Cancel extends Command {
    @Autowired
    private UserCommandService userCommandService;

    @Override
    public void acceptWithException(Message message) throws TelegramApiException, URISyntaxException, IOException {
        userCommandService.removeUserCommand(message.getFrom().getId());
        reply(message, "responses/cancel.md");
    }
}
