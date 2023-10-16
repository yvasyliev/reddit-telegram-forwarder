package com.github.yvasyliev.service.telegram.callbacks;

import com.github.yvasyliev.model.dto.RedditPostDecisionData;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

@Service("/rejectredditpost")
public class RejectRedditPost extends Callback {
    @Override
    public void acceptWithException(CallbackQuery callbackQuery) throws Exception {
        var message = callbackQuery.getMessage();
        var chatId = message.getChatId();
        var messageId = message.getMessageId();
        var data = objectMapper.readValue(callbackQuery.getData(), RedditPostDecisionData.class);
        redTelBot.rejectPostCandidate(data.created());
        redTelBot.execute(new EditMessageReplyMarkup(
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
        redTelBot.execute(editMessageText);
    }
}
