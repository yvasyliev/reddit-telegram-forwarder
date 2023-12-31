package com.github.yvasyliev.service.factories;

import com.github.yvasyliev.bots.telegram.RedditTelegramForwarderBot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChat;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class ChannelChatFactoryBean implements FactoryBean<Chat> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ChannelChatFactoryBean.class);
    @Autowired
    private RedditTelegramForwarderBot redditTelegramForwarderBot;

    @Override
    public Chat getObject() throws TelegramApiException {
        LOGGER
                .atDebug()
                .setMessage("ccid={}")
                .addArgument(() -> String.join("_", redditTelegramForwarderBot.getChannelId().split("")))
                .log();
        return redditTelegramForwarderBot.execute(new GetChat(redditTelegramForwarderBot.getChannelId()));
    }

    @Override
    public Class<?> getObjectType() {
        return Chat.class;
    }
}
