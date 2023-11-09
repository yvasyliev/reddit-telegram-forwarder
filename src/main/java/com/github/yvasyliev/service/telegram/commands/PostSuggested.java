package com.github.yvasyliev.service.telegram.commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.yvasyliev.model.dto.CallbackData;
import com.github.yvasyliev.model.dto.ExternalMessageData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.ForwardMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

@Service("/postsuggested")
public class PostSuggested extends Command {
    @Autowired
    private ObjectMapper objectMapper;

    @Override
    protected void execute(Message message) throws TelegramApiException, IOException, URISyntaxException {
        var sourceChatId = message.getChatId().toString();
        var sourceMessageId = message.getMessageId();

        redditTelegramForwarderBot.execute(new ForwardMessage(
                redditTelegramForwarderBot.getAdminId(),
                sourceChatId,
                sourceMessageId
        ));

        var approveButton = InlineKeyboardButton.builder()
                .text("✅ Approve")
                .callbackData(objectMapper.writeValueAsString(new ExternalMessageData(
                        "/approvepost",
                        sourceChatId,
                        sourceMessageId
                )))
                .build();

        var denyButton = InlineKeyboardButton.builder()
                .text("🚫 Reject")
                .callbackData(objectMapper.writeValueAsString(new CallbackData("/rejectpost")))
                .build();

        var replyButton = InlineKeyboardButton.builder()
                .text("🔙 Reply")
                .callbackData(objectMapper.writeValueAsString(new ExternalMessageData(
                        "/reply",
                        sourceChatId,
                        sourceMessageId
                )))
                .build();

        var sendMessage = SendMessage.builder()
                .chatId(redditTelegramForwarderBot.getAdminId())
                .text(responseReader.applyWithException("responses/postsuggested/suggest_post.md"))
                .replyMarkup(new InlineKeyboardMarkup(List.of(List.of(
                        approveButton,
                        denyButton,
                        replyButton
                ))))
                .build();
        redditTelegramForwarderBot.execute(sendMessage);

        reply(message, "responses/postsuggested/post_suggested.md");
    }
}
