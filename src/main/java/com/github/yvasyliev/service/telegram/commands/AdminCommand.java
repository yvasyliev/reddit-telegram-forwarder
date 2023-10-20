package com.github.yvasyliev.service.telegram.commands;

import org.telegram.telegrambots.meta.api.objects.Message;

public abstract class AdminCommand extends Command {
    @Override
    protected boolean hasPermission(Message message) {
        return redTelBot.isAdmin(message.getFrom());
    }
}
