package com.github.yvasyliev.bots.telegram;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.yvasyliev.model.dto.CallbackData;
import com.github.yvasyliev.model.dto.ExternalMessageData;
import com.github.yvasyliev.service.telegram.PostManager;
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
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.BotSession;

import java.util.Map;
import java.util.Optional;

public class RedTelBot extends AbstractRedTelBot {
    private static final Logger LOGGER = LoggerFactory.getLogger(RedTelBot.class);

    @Autowired
    private ApplicationContext context;

    @Autowired
    private ObjectMapper objectMapper;

    private BotSession botSession;

    @Autowired
    private Map<Long, String> userCommands;

    @Autowired
    private Map<Long, ExternalMessageData> awaitingReplies;

    @Autowired
    private PostManager postManager;

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
    public void onUpdateReceived(Update update) {
        LOGGER.debug("Update received: {}", update);
        if (update.hasMessage()) {
            onMessageReceived(update.getMessage());
        } else if (update.hasCallbackQuery()) {
            onCallbackQueryReceived(update.getCallbackQuery());
        }
    }

    public void onMessageReceived(Message message) {
        var isAutomaticForward = message.getIsAutomaticForward();
        var forwardFromMessageId = message.getForwardFromMessageId();
        if (isAutomaticForward != null && isAutomaticForward && forwardFromMessageId != null) {
            postManager.sendExtraPhotos(message.getMessageId(), forwardFromMessageId);
        } else if (message.isUserMessage()) {
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
        if (message.isUserMessage() && isFromAdmin(callbackQuery)) {
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

    public String addUserCommand(long userId, String command) {
        return userCommands.put(userId, command);
    }

    public String removeUserCommand(long userId) {
        return userCommands.remove(userId);
    }

    public ExternalMessageData addAwaitingReply(Long userId, ExternalMessageData messageData) {
        return awaitingReplies.put(userId, messageData);
    }

    public ExternalMessageData getAwaitingReply(Long userId) {
        return awaitingReplies.remove(userId);
    }

    public boolean isFromAdmin(CallbackQuery callbackQuery) {
        return isAdmin(callbackQuery.getFrom());
    }

    public boolean isAdmin(User user) {
        return user.getId().toString().equals(getAdminId());
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
}
