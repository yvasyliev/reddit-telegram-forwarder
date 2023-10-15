package com.github.yvasyliev.service.telegram.callbacks;

import com.github.yvasyliev.model.dto.PostApprovedData;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.ForwardMessage;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.net.URISyntaxException;

@Service("/approvepost")
public class ApprovePost extends Callback {
    @Override
    public void acceptWithException(CallbackQuery callbackQuery) throws TelegramApiException, URISyntaxException, IOException {
        var postApprovedData = objectMapper.readValue(callbackQuery.getData(), PostApprovedData.class);
        redTelBot.execute(new ForwardMessage(
                redTelBot.getChannelId(),
                postApprovedData.fromChatId(),
                postApprovedData.messageId()
        ));
        redTelBot.execute(new EditMessageReplyMarkup(
                callbackQuery.getMessage().getChatId().toString(),
                callbackQuery.getMessage().getMessageId(),
                callbackQuery.getInlineMessageId(),
                null
        ));
        var editMessageText = EditMessageText.builder()
                .chatId(callbackQuery.getMessage().getChatId())
                .messageId(callbackQuery.getMessage().getMessageId())
                .text(format("responses/approvepost.md", callbackQuery.getMessage().getText()))
                .parseMode(ParseMode.MARKDOWNV2)
                .build();
        redTelBot.execute(editMessageText);
    }
}
