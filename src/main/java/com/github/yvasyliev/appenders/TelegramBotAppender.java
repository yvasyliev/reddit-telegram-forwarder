package com.github.yvasyliev.appenders;

import com.github.yvasyliev.RedditTelegramForwarderApplication;
import com.github.yvasyliev.bots.telegram.notifier.TelegramNotifier;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Core;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.Property;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutionException;

@Plugin(name = "TelegramBotAppender", category = Core.CATEGORY_NAME, elementType = Appender.ELEMENT_TYPE)
public class TelegramBotAppender extends AbstractAppender {
    private static final Logger LOGGER = LoggerFactory.getLogger(TelegramBotAppender.class);

    private static final String MESSAGE_TEMPLATE = """
            %s
            %s""";

    private final Set<Class<? extends Throwable>> ignoredExceptions;

    protected TelegramBotAppender(String name, Set<Class<? extends Throwable>> ignoredExceptions) {
        super(name, null, PatternLayout.createDefaultLayout(), true, Property.EMPTY_ARRAY);
        this.ignoredExceptions = ignoredExceptions;
    }

    @PluginFactory
    public static TelegramBotAppender createAppender(
            @PluginAttribute("name") String name,
            @PluginElement("IgnoredExceptions") IgnoredExceptions ignoredExceptions) {
        return new TelegramBotAppender(name, ignoredExceptions.getExceptionClasses());
    }

    @Override
    public void append(LogEvent event) {
        var thrown = event.getThrown() instanceof ExecutionException executionException
                ? executionException.getCause()
                : event.getThrown();
        if (thrown == null || !ignoredExceptions.contains(thrown.getClass())) {
            RedditTelegramForwarderApplication.withContext(applicationContext -> {
                try {
                    applicationContext
                            .getBean(TelegramNotifier.class)
                            .applyWithException(buildMessage(
                                    event.getMessage().getFormattedMessage(),
                                    thrown
                            ));
                } catch (Exception e) {
                    LOGGER.warn("Failed to send notification", e);
                }
            });
        }
    }

    private String buildMessage(String message, Throwable throwable) {
        return getStackTrace(throwable)
                .map(stackTrace -> MESSAGE_TEMPLATE.formatted(message, stackTrace))
                .orElse(message);
    }

    private Optional<String> getStackTrace(Throwable throwable) {
        return Optional.ofNullable(throwable).map(t -> {
            try (var stringWriter = new StringWriter(); var printWriter = new PrintWriter(stringWriter)) {
                t.printStackTrace(printWriter);
                return stringWriter.toString();
            } catch (IOException e) {
                LOGGER.warn("Failed to build stack trace.", e);
                return null;
            }
        });
    }
}
