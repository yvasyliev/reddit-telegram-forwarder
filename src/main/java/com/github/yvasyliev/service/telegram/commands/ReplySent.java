package com.github.yvasyliev.service.telegram.commands;

import com.github.yvasyliev.service.telegram.MarkdownV2Escaper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.net.URISyntaxException;

@Service("/replysent")
public class ReplySent extends AdminCommand {
    @Autowired
    private MarkdownV2Escaper markdownV2Escaper;

    @Override
    public void execute(Message message) throws URISyntaxException, IOException, TelegramApiException {
        if (message.hasText()) {
            var userId = message.getFrom().getId();
            redditTelegramForwarderBot.removeUserCommand(userId);
            var awaitingReply = redditTelegramForwarderBot.getAwaitingReply(userId);
            var replyMessage = SendMessage.builder()
                    .chatId(awaitingReply.fromChatId())
                    .replyToMessageId(awaitingReply.messageId())
                    .text(responseReader.applyWithException("responses/replysent/reply_template.md").formatted(markdownV2Escaper.apply(message.getText())))
                    .parseMode(ParseMode.MARKDOWNV2)
                    .build();
            redditTelegramForwarderBot.execute(replyMessage);
            reply(message, "responses/replysent/reply_sent.md");
        } else {
            reply(message, "responses/replysent/empty_reply.md");
        }
    }
}
