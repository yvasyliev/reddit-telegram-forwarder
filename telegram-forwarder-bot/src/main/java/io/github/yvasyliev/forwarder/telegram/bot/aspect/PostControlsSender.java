package io.github.yvasyliev.forwarder.telegram.bot.aspect;

import io.github.yvasyliev.forwarder.telegram.bot.configuration.PostControlsSendMessageProperties;
import io.github.yvasyliev.forwarder.telegram.bot.mapper.SendMessageMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.List;

/**
 * Aspect that sends a post control keyboard after sending a post or media group.
 * It builds the keyboard buttons based on the configured post controls and sends it to the chat.
 */
@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class PostControlsSender {
    private final TelegramClient telegramClient;
    private final SendMessageMapper sendMessageMapper;
    private final PostControlsSendMessageProperties postControlsSendMessageProperties;

    /**
     * After returning advice that sends a post control keyboard after a post or a media group was sent, either
     * directly through the legacy {@code PostSender} or through the {@code PostSendingHandler} of the post sending
     * integration flow. The return value is bound as {@link Object} because the advised method declares an
     * {@link Object} return type, which would never match a generic {@code returning} parameter.
     *
     * @param post the sent {@link Message} or {@link List} of {@link Message}
     */
    @AfterReturning(
            pointcut = "io.github.yvasyliev.forwarder.telegram.bot.util.Pointcuts.postSent()",
            returning = "post"
    )
    public void sendPostControlKeyboard(Object post) {
        if (post == null) {
            log.debug("No post was sent, skipping post controls");

            return;
        }

        switch (post) {
            case Message message -> sendPostControlKeyboard(List.of(message));
            case List<?> messageList -> {
                var messages = messageList.stream()
                        .filter(Message.class::isInstance)
                        .map(Message.class::cast)
                        .toList();

                if (!messages.isEmpty()) {
                    sendPostControlKeyboard(messages);
                }
            }
            default -> log.warn("Unsupported post {}", post);
        }
    }

    /**
     * Sends a post control keyboard after sending a post or a media group.
     *
     * @param messages the {@link List} of {@link Message} that were sent
     */
    public void sendPostControlKeyboard(List<Message> messages) {
        var sendMessage = sendMessageMapper.map(postControlsSendMessageProperties, messages);

        try {
            telegramClient.execute(sendMessage);
        } catch (TelegramApiException e) {
            log.error("Failed to send post controls", e);
        }
    }
}
