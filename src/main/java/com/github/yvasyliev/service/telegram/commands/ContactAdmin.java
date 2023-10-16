package com.github.yvasyliev.service.telegram.commands;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;

@Service("/contactadmin")
public class ContactAdmin extends Command {
    @Override
    public void acceptWithException(Message message) throws Exception {
        redTelBot.addUserCommand(message.getFrom().getId(), "/textadmin");
        reply(message, "responses/contactadmin.md");
    }
}
