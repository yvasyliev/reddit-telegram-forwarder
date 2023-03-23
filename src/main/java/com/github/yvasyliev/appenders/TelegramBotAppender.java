package com.github.yvasyliev.appenders;

import com.github.yvasyliev.config.ApplicationContextHolder;
import com.github.yvasyliev.telegram.TelegramRepeaterBot;
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
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;

@Plugin(name = "TelegramBotAppender", category = Core.CATEGORY_NAME, elementType = Appender.ELEMENT_TYPE)
public class TelegramBotAppender extends AbstractAppender {

    protected TelegramBotAppender(String name, Filter filter, Layout<? extends Serializable> layout, boolean ignoreExceptions, Property[] properties) {
        super(name, filter, layout, ignoreExceptions, properties);
    }

    @PluginFactory
    public static TelegramBotAppender createAppender(@PluginAttribute("name") String name, @PluginElement("Filter") Filter filter) {
        return new TelegramBotAppender(name, filter, PatternLayout.createDefaultLayout(), true, Property.EMPTY_ARRAY);
    }

    @Override
    public void append(LogEvent event) {
        var formattedMessage = event.getMessage().getFormattedMessage();

        try {
            var stackTrace = getStackTrace(event.getThrown());
            if (stackTrace != null) {
                formattedMessage += "\n" + stackTrace;
            }

            var telegramRepeaterBot = ApplicationContextHolder
                    .getInstance()
                    .getApplicationContext()
                    .getBean(TelegramRepeaterBot.class);
            telegramRepeaterBot.sendDeveloperMessage(formattedMessage);
        } catch (TelegramApiException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String getStackTrace(Throwable throwable) throws IOException {
        if (throwable == null) {
            return null;
        }

        try (var stringWriter = new StringWriter(); var printWriter = new PrintWriter(stringWriter)) {
            throwable.printStackTrace(printWriter);
            return stringWriter.toString();
        }
    }
}
