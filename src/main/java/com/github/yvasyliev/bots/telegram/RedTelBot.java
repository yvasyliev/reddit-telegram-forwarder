package com.github.yvasyliev.bots.telegram;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.yvasyliev.model.dto.CallbackData;
import com.github.yvasyliev.service.telegram.callbacks.Callback;
import com.github.yvasyliev.service.telegram.commands.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.BotSession;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class RedTelBot extends TelegramPublisher {
    private static final Logger LOGGER = LoggerFactory.getLogger(RedTelBot.class);

    @Autowired
    private ApplicationContext context;

    @Autowired
    private ObjectMapper objectMapper;

    private BotSession botSession;

    private final Map<Long, String> userCommands = new ConcurrentHashMap<>();

    public RedTelBot(@Value("${BOT_TOKEN}") String botToken) {
        super(botToken);
    }

    public void startPolling() throws TelegramApiException {
        botSession = context.getBean(TelegramBotsApi.class).registerBot(this);
    }

    public void stopPolling() {
        botSession.stop();
    }

    @Override
    public void onUpdatesReceived(List<Update> updates) {
        var messages = updates
                .stream()
                .filter(Update::hasMessage)
                .map(Update::getMessage)
                .filter(Message::getIsAutomaticForward)
                .toList();
        if (messages.size() == 10) {
            var message = messages.get(0);
            sendExtraPhotos(message.getMessageId(), message.getForwardFromMessageId());
        }
        super.onUpdatesReceived(updates);
    }

    @Override
    public void onUpdateReceived(Update update) {
        LOGGER.debug("Update received: {}", update);
        if (update.hasMessage()) {
            onMessageReceived(update.getMessage());
        } else if (update.hasCallbackQuery()) {
            onCallbackQueryReceived(update.getCallbackQuery());
        }
    }

    public void onMessageReceived(Message message) {
        if (message.isUserMessage()) {
            onUserMessageReceived(message);
        }
    }

    public void onUserMessageReceived(Message message) {
        getCommand(message).ifPresent(command -> onCommandReceived(command, message));
    }

    public void onCommandReceived(String command, Message message) {
        if (context.containsBean(command)) {
            try {
                context.getBean(command, Command.class).acceptWithException(message);
            } catch (Exception e) {
                LOGGER.error("Failed to perform command {}", command, e);
            }
        } else {
            LOGGER.info("Unknown command: {}", command);
        }
    }

    public void onCallbackQueryReceived(CallbackQuery callbackQuery) {
        var message = callbackQuery.getMessage();
        if (message.isUserMessage() && getAdminId().equals(callbackQuery.getFrom().getId().toString())) {
            try {
                var callbackData = objectMapper.readValue(callbackQuery.getData(), CallbackData.class);
                context.getBean(callbackData.action(), Callback.class).acceptWithException(callbackQuery);
            } catch (JsonProcessingException e) {
                LOGGER.error("Failed to parse callback data {}", callbackQuery.getData(), e);
            } catch (Exception e) {
                LOGGER.error("Failed to handle callback", e);
            }
        }
    }

    private Optional<String> getCommand(Message message) {
        long userId = message.getFrom().getId();
        if (message.hasText()) {
            var text = message.getText().trim();
            if (looksLikeCommand(text)) {
                removeUserCommand(userId);
                return Optional.of(text);
            }
        }

        return Optional.ofNullable(userCommands.get(userId));
    }

    private boolean looksLikeCommand(String text) {
        return text.matches("/\\w+");
    }

    public String addUserCommand(long userId, String command) {
        return userCommands.put(userId, command);
    }

    public String removeUserCommand(long userId) {
        return userCommands.remove(userId);
    }
}
