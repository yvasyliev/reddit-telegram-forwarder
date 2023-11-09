package com.github.yvasyliev.bots.telegram;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.yvasyliev.model.dto.CallbackData;
import com.github.yvasyliev.model.dto.ChannelPost;
import com.github.yvasyliev.model.dto.ExternalMessageData;
import com.github.yvasyliev.model.events.NewChannelPostEvent;
import com.github.yvasyliev.service.async.DelayedBlockingExecutor;
import com.github.yvasyliev.service.telegram.callbacks.Callback;
import com.github.yvasyliev.service.telegram.commands.Command;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMediaGroup;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.generics.BotSession;
import org.telegram.telegrambots.starter.AfterBotRegistration;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Component
public class RedditTelegramForwarderBot extends TelegramLongPollingBot {
    private static final Logger LOGGER = LoggerFactory.getLogger(RedditTelegramForwarderBot.class);

    @Autowired
    private ApplicationContext context;

    @Autowired
    private ObjectMapper objectMapper;

    private BotSession botSession;

    @Autowired
    @Qualifier("synchronizedFixedSizeMap")
    private Map<Long, String> userCommands;

    @Autowired
    @Qualifier("synchronizedFixedSizeMap")
    private Map<Long, ExternalMessageData> awaitingReplies;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    private DelayedBlockingExecutor delayedBlockingExecutor;

    @Value("${telegram.chat.id}")
    private String chatId;

    @Value("${telegram.channel.id}")
    private String channelId;

    @Value("${telegram.admin.id}")
    private String adminId;

    @Value("${telegram.bot.username}")
    private String botUsername;

    public RedditTelegramForwarderBot(@Value("${telegram.bot.token}") String botToken) {
        super(botToken);
    }

    @AfterBotRegistration
    public void setBotSession(BotSession botSession) {
        this.botSession = botSession;
        LOGGER.info("{} started long polling.", getBotUsername());
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @PreDestroy
    public void stopPolling() {
        botSession.stop();
        LOGGER.info("{} stopped long polling.", getBotUsername());
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
            eventPublisher.publishEvent(new NewChannelPostEvent(new ChannelPost(message.getMessageId(), forwardFromMessageId)));
        } else if (message.isUserMessage()) {
            onUserMessageReceived(message);
        }
    }

    public void onCallbackQueryReceived(CallbackQuery callbackQuery) {
        var message = callbackQuery.getMessage();
        if (message.isUserMessage() && isAdmin(callbackQuery.getFrom())) {
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

    public void onUserMessageReceived(Message message) {
        getCommand(message).ifPresent(command -> onCommandReceived(command, message));
    }

    public boolean isAdmin(User user) {
        return user.getId().toString().equals(getAdminId());
    }

    private Optional<String> getCommand(Message message) {
        var userId = message.getFrom().getId();
        if (message.isCommand()) {
            return Optional.ofNullable(removeUserCommand(userId))
                    .or(() -> Optional.of(message.getText().trim()));
        }
        return Optional.ofNullable(userCommands.get(userId));
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

    public String getAdminId() {
        return adminId;
    }

    public String removeUserCommand(long userId) {
        return userCommands.remove(userId);
    }

    public String addUserCommand(long userId, String command) {
        return userCommands.put(userId, command);
    }

    public ExternalMessageData addAwaitingReply(Long userId, ExternalMessageData messageData) {
        return awaitingReplies.put(userId, messageData);
    }

    public ExternalMessageData getAwaitingReply(Long userId) {
        return awaitingReplies.remove(userId);
    }

    public CompletableFuture<List<Message>> executeDelayed(SendMediaGroup sendMediaGroup) {
        return delayedBlockingExecutor.submit(() -> execute(sendMediaGroup));
    }

    public CompletableFuture<Message> executeDelayed(SendPhoto sendPhoto) {
        return delayedBlockingExecutor.submit(() -> execute(sendPhoto));
    }

    public String getChatId() {
        return chatId;
    }

    public String getChannelId() {
        return channelId;
    }
}
