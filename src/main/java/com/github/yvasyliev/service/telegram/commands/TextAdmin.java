package com.github.yvasyliev.service.telegram.commands;

import com.fasterxml.jackson.databind.ObjectMapper;
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

@Service("/textadmin")
public class TextAdmin extends Command {
    @Autowired
    private ObjectMapper objectMapper;

    @Override
    protected void execute(Message message) throws TelegramApiException, IOException, URISyntaxException {
        redditTelegramForwarderBot.removeUserCommand(message.getFrom().getId());
        var sourceChatId = message.getChatId();
        var sourceMessageId = message.getMessageId();
        var forwardMessage = ForwardMessage.builder()
                .chatId(redditTelegramForwarderBot.getAdminId())
                .fromChatId(sourceChatId)
                .messageId(sourceMessageId)
                .build();
        redditTelegramForwarderBot.execute(forwardMessage);
        var replyButton = InlineKeyboardButton.builder()
                .text("🔙 Reply")
                .callbackData(objectMapper.writeValueAsString(new ExternalMessageData(
                        "/reply",
                        sourceChatId.toString(),
                        sourceMessageId
                )))
                .build();
        var sendMessage = SendMessage.builder()
                .chatId(redditTelegramForwarderBot.getAdminId())
                .text(responseReader.applyWithException("responses/textadmin/message_to_admin.md"))
                .replyMarkup(new InlineKeyboardMarkup(List.of(List.of(replyButton))))
                .build();
        redditTelegramForwarderBot.execute(sendMessage);
        reply(message, "responses/textadmin/message_sent.md");
    }
}
