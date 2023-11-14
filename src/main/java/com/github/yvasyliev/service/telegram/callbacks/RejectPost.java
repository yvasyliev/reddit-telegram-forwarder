package com.github.yvasyliev.service.telegram.callbacks;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

@Service("/rejectpost")
public class RejectPost extends Callback {
    @Override
    public void acceptWithException(CallbackQuery callbackQuery) throws Exception {
        var message = callbackQuery.getMessage();
        var chatId = message.getChatId();
        var messageId = message.getMessageId();
        var answerCallbackQuery = AnswerCallbackQuery.builder()
                .callbackQueryId(callbackQuery.getId())
                .text("Rejecting post...")
                .build();
        redditTelegramForwarderBot.execute(answerCallbackQuery);
        redditTelegramForwarderBot.execute(new EditMessageReplyMarkup(
                chatId.toString(),
                messageId,
                callbackQuery.getInlineMessageId(),
                null
        ));
        var editMessageText = EditMessageText.builder()
                .chatId(chatId)
                .messageId(messageId)
                .text(format("responses/rejectpost.md", message.getText()))
                .parseMode(ParseMode.MARKDOWNV2)
                .build();
        redditTelegramForwarderBot.execute(editMessageText);
    }
}
