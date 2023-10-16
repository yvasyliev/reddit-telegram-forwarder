package com.github.yvasyliev.service.telegram.callbacks;

import com.github.yvasyliev.model.dto.ExternalMessageData;
import com.github.yvasyliev.service.telegram.readers.BotResponseReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.net.URISyntaxException;

@Service("/reply")
public class Reply extends Callback {
    @Autowired
    private BotResponseReader responseReader;

    @Override
    public void acceptWithException(CallbackQuery callbackQuery) throws IOException, URISyntaxException, TelegramApiException {
        var messageData = objectMapper.readValue(callbackQuery.getData(), ExternalMessageData.class);
        var userId = callbackQuery.getFrom().getId();
        redTelBot.addUserCommand(userId, "/replysent");
        redTelBot.addAwaitingReply(userId, messageData);
        var sendMessage = SendMessage.builder()
                .chatId(userId)
                .text(responseReader.applyWithException("responses/reply.md"))
                .parseMode(ParseMode.MARKDOWNV2)
                .build();
        redTelBot.execute(sendMessage);
    }
}
