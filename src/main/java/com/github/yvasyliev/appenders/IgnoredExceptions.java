package com.github.yvasyliev.appenders;

import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Core;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Plugin(name = "IgnoredExceptions", category = Core.CATEGORY_NAME, elementType = Appender.ELEMENT_TYPE)
public class IgnoredExceptions {
    private final Set<Class<? extends Throwable>> exceptionClasses;

    public IgnoredExceptions(Set<Class<? extends Throwable>> exceptionClasses) {
        this.exceptionClasses = exceptionClasses;
    }

    @PluginFactory
    public static IgnoredExceptions createAppender(@PluginElement("IgnoredException") IgnoredException[] ignoredExceptions) {
        return new IgnoredExceptions(
                Arrays
                        .stream(ignoredExceptions)
                        .map(IgnoredException::getExceptionClass)
                        .collect(Collectors.toSet())
        );
    }

    public Set<Class<? extends Throwable>> getExceptionClasses() {
        return exceptionClasses;
    }
}
