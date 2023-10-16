package com.github.yvasyliev.service.telegram.commands;

import com.github.yvasyliev.utils.MarkdownV2;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.net.URISyntaxException;

@Service("/replysent")
public class ReplySent extends Command {
    @Override
    public void acceptWithException(Message message) throws URISyntaxException, IOException, TelegramApiException {
        if (message.hasText()) {
            var userId = message.getFrom().getId();
            redTelBot.removeUserCommand(userId);
            var awaitingReply = redTelBot.getAwaitingReply(userId);
            var replyMessage = SendMessage.builder()
                    .chatId(awaitingReply.fromChatId())
                    .replyToMessageId(awaitingReply.messageId())
                    .text(responseReader.applyWithException("responses/replysent/reply_template.md").formatted(MarkdownV2.escaped(message.getText())))
                    .parseMode(ParseMode.MARKDOWNV2)
                    .build();
            redTelBot.execute(replyMessage);
            reply(message, "responses/replysent/reply_sent.md");
        } else {
            reply(message, "responses/replysent/empty_reply.md");
        }
    }
}
