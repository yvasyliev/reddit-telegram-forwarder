package com.github.yvasyliev.config;

import com.github.yvasyliev.properties.AppProperties;
import com.github.yvasyliev.telegram.AnadeArmasFanbot;
import com.github.masecla.RedditClient;
import com.github.masecla.config.RedditClientConfig;
import com.github.masecla.config.ScriptClientConfig;
import com.github.masecla.objects.app.script.Credentials;
import com.github.masecla.objects.app.script.PersonalUseScript;
import com.github.masecla.objects.app.script.UserAgent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

@Configuration
public class AppConfig {
    @Bean
    public String appPropertiesPath() {
        return "config.properties";
    }

    @Bean
    public Properties appProperties() {
        return new AppProperties();
    }

    @Bean
    public PersonalUseScript personalUseScript() {
        return new PersonalUseScript(
                appProperties().getProperty("clientId"),
                appProperties().getProperty("clientSecret")
        );
    }

    @Bean
    public UserAgent userAgent() {
        return new UserAgent(
                appProperties().getProperty("appName"),
                appProperties().getProperty("version"),
                appProperties().getProperty("redditUsername")
        );
    }

    @Bean
    public Credentials credentials() {
        return new Credentials(
                appProperties().getProperty("username"),
                appProperties().getProperty("password")
        );
    }

    @Bean
    public RedditClientConfig redditClientConfig() {
        return new ScriptClientConfig(
                personalUseScript(),
                userAgent(),
                credentials()
        );
    }

    @Bean
    public RedditClient redditClient() {
        return new RedditClient(redditClientConfig());
    }

    @Bean
    public AnadeArmasFanbot anadeArmasFanbot() {
        return new AnadeArmasFanbot();
    }
}