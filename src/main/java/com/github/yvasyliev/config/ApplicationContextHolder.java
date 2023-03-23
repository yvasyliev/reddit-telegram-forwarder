package com.github.yvasyliev.config;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public final class ApplicationContextHolder {
    private static final ApplicationContextHolder INSTANCE = new ApplicationContextHolder();

    private final ApplicationContext applicationContext;

    private ApplicationContextHolder() {
        var context = new AnnotationConfigApplicationContext(AppConfig.class);
        context.registerShutdownHook();
        this.applicationContext = context;
    }

    public static ApplicationContextHolder getInstance() {
        return INSTANCE;
    }

    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }
}
