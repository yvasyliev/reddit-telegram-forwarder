package io.github.yvasyliev.forwarder.telegram.bot.util;

import org.aspectj.lang.annotation.Pointcut;

/**
 * Defines pointcuts for AOP in the Telegram Forwarder Bot application.
 */
public class Pointcuts {
    /**
     * Pointcut for handling Telegram message events.
     */
    @Pointcut("execution(void io.github.yvasyliev.forwarder.telegram.bot.service.TelegramEventHandler.handle("
            + "org.telegram.telegrambots.meta.api.objects.message.Message))")
    public void handleMessageEvent() {}

    /**
     * Pointcut for handling Telegram callback query events.
     */
    @Pointcut("execution(void io.github.yvasyliev.forwarder.telegram.bot.service.TelegramEventHandler.handle("
            + "org.telegram.telegrambots.meta.api.objects.CallbackQuery))")
    public void handleCallbackQueryEvent() {}

    /**
     * Pointcut for executing a callback query command.
     */
    @Pointcut("execution(void io.github.yvasyliev.forwarder.telegram.bot.service.command.CallbackQueryCommand.execute("
            + "org.telegram.telegrambots.meta.api.objects.CallbackQuery, "
            + "io.github.yvasyliev.forwarder.telegram.bot.dto.CommandCallbackData))")
    public void executeCallbackQueryCommand() {}

    /**
     * Pointcut for sending a post or a media group, either directly through {@code PostSender} or through
     * {@code PostSendingHandler} of the post sending integration flow. Both methods are matched with a wildcard
     * return type, because the advised return value is bound to an {@link Object} advice parameter anyway.
     */
    @Pointcut("execution(* io.github.yvasyliev.forwarder.telegram.core.service.PostSender.send(..))"
            + " || execution(* io.github.yvasyliev.forwarder.telegram.bot.service.PostSendingHandler.handle(..))")
    public void postSent() {}

    /**
     * Pointcut for executing the post controls command.
     */
    @Pointcut("execution(void "
            + "io.github.yvasyliev.forwarder.telegram.bot.service.command.PostControlsCallbackQueryCommand.execute("
            + "org.telegram.telegrambots.meta.api.objects.CallbackQuery, *))")
    public void executePostControlsCallbackQueryCommand() {}
}
