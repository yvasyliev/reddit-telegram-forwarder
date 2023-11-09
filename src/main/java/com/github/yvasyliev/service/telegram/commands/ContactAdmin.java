package com.github.yvasyliev.service.telegram.commands;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;

@Service("/contactadmin")
public class ContactAdmin extends Command {
    @Override
    protected void execute(Message message) throws Exception {
        redditTelegramForwarderBot.addUserCommand(message.getFrom().getId(), "/textadmin");
        reply(message, "responses/contactadmin.md");
    }
}
