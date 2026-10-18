package io.github.yvasyliev.forwarder.telegram.bot.service;

import io.github.yvasyliev.forwarder.telegram.bot.mapper.PartialBotApiMethodMapper;
import io.github.yvasyliev.forwarder.telegram.core.configuration.TelegramAdminProperties;
import io.github.yvasyliev.forwarder.telegram.core.dto.PartialBotApiMethodDTO;
import io.github.yvasyliev.forwarder.telegram.core.util.CloseableSupplier;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.integration.core.GenericHandler;
import org.springframework.messaging.MessageHeaders;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendAnimation;
import org.telegram.telegrambots.meta.api.methods.send.SendMediaGroup;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.send.SendVideo;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.io.IOException;

/**
 * Service for sending posts to Telegram using various Bot API methods.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PostSendingHandler implements GenericHandler<CloseableSupplier<PartialBotApiMethodDTO>> {
    private final TelegramAdminProperties adminProperties;
    private final PartialBotApiMethodMapper botApiMethodMapper;
    private final TelegramClient telegramClient;

    /**
     * Handles the sending of a post to Telegram based on the provided payload and message headers.
     *
     * @param payload the supplier providing the {@link PartialBotApiMethodDTO} to be sent
     * @param headers the message headers associated with the request
     * @return the result of the Telegram API method execution, or null if an error occurred
     * @throws IllegalArgumentException when the provided {@link PartialBotApiMethodDTO} cannot be handled
     */
    @Override
    public @Nullable Object handle(CloseableSupplier<PartialBotApiMethodDTO> payload, MessageHeaders headers) {
        try (var methodDTO = payload.get()) {
            return switch (botApiMethodMapper.map(methodDTO, adminProperties)) {
                case SendAnimation sendAnimation -> telegramClient.execute(sendAnimation);
                case SendMediaGroup sendMediaGroup -> telegramClient.execute(sendMediaGroup);
                case SendMessage sendMessage -> telegramClient.execute(sendMessage);
                case SendPhoto sendPhoto -> telegramClient.execute(sendPhoto);
                case SendVideo sendVideo -> telegramClient.execute(sendVideo);
                default -> throw new IllegalArgumentException("Unsupported Telegram Bot API method: " + methodDTO);
            };
        } catch (IOException | TelegramApiException e) {
            log.error("Failed to send post to Telegram", e);
        }

        return null;
    }
}
