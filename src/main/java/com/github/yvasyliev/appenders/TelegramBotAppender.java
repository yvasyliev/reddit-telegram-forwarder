package com.github.yvasyliev.appenders;

import com.github.yvasyliev.RedditTelegramForwarderApplication;
import com.github.yvasyliev.bots.telegram.notifier.TelegramNotifier;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Core;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.Property;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.layout.PatternLayout;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.util.Optional;

@Plugin(name = "TelegramBotAppender", category = Core.CATEGORY_NAME, elementType = Appender.ELEMENT_TYPE)
public class TelegramBotAppender extends AbstractAppender {
    private static final String MESSAGE_TEMPLATE = """
            %s
            %s""";

    protected TelegramBotAppender(String name, Filter filter, Layout<? extends Serializable> layout, boolean ignoreExceptions, Property[] properties) {
        super(name, filter, layout, ignoreExceptions, properties);
    }

    @PluginFactory
    public static TelegramBotAppender createAppender(@PluginAttribute("name") String name, @PluginElement("Filter") Filter filter) {
        return new TelegramBotAppender(name, filter, PatternLayout.createDefaultLayout(), true, Property.EMPTY_ARRAY);
    }

    @Override
    public void append(LogEvent event) {
        RedditTelegramForwarderApplication.withContext(applicationContext -> {
            try {
                applicationContext
                        .getBean(TelegramNotifier.class)
                        .applyWithException(buildMessage(event));
            } catch (Exception e) {
                e.printStackTrace(System.err);
            }
        });
    }

    private String buildMessage(LogEvent event) {
        var formattedMessage = event.getMessage().getFormattedMessage();
        return getStackTrace(event.getThrown())
                .map(stackTrace -> MESSAGE_TEMPLATE.formatted(formattedMessage, stackTrace))
                .orElse(formattedMessage);
    }

    private Optional<String> getStackTrace(Throwable throwable) {
        return Optional.ofNullable(throwable).map(t -> {
            try (var stringWriter = new StringWriter(); var printWriter = new PrintWriter(stringWriter)) {
                t.printStackTrace(printWriter);
                return stringWriter.toString();
            } catch (IOException e) {
                e.printStackTrace(System.err);
                return null;
            }
        });
    }
}
