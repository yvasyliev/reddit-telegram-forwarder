package com.github.yvasyliev.appenders;

import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Core;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.config.plugins.PluginValue;

@Plugin(name = "IgnoredException", category = Core.CATEGORY_NAME, elementType = Appender.ELEMENT_TYPE)
public class IgnoredException {
    private final Class<? extends Throwable> exceptionClass;

    public IgnoredException(Class<? extends Throwable> exceptionClass) {
        this.exceptionClass = exceptionClass;
    }

    @PluginFactory
    public static IgnoredException createAppender(@PluginValue("value") String name) throws ClassNotFoundException {
        return new IgnoredException(Class.forName(name).asSubclass(Throwable.class));
    }

    public Class<? extends Throwable> getExceptionClass() {
        return exceptionClass;
    }
}
