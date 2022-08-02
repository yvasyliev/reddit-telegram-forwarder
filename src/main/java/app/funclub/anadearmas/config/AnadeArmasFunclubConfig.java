package app.funclub.anadearmas.config;

import app.funclub.anadearmas.exceptions.PropertiesReaderException;
import app.funclub.anadearmas.exceptions.PropertiesWriterException;
import app.funclub.anadearmas.services.PropertiesReader;
import app.funclub.anadearmas.services.PropertiesWriter;
import app.funclub.anadearmas.services.impl.PropertiesReaderImpl;
import app.funclub.anadearmas.services.impl.PropertiesWriterImpl;
import com.github.masecla.RedditClient;
import com.github.masecla.config.RedditClientConfig;
import com.github.masecla.config.ScriptClientConfig;
import com.github.masecla.objects.app.script.Credentials;
import com.github.masecla.objects.app.script.PersonalUseScript;
import com.github.masecla.objects.app.script.UserAgent;

import java.util.Properties;

public class AnadeArmasFunclubConfig {
    private final String appConfigPath;
    private Properties appConfig;

    public AnadeArmasFunclubConfig(String appConfigPath) {
        this.appConfigPath = appConfigPath;
    }

    public void postConstruct() throws PropertiesReaderException {
        this.appConfig = propertiesReader().read(appConfigPath);
    }

    public void preDestroy() throws PropertiesWriterException {
        propertiesWriter().write(appConfigPath, appConfig);
    }

    public PropertiesReader propertiesReader() {
        return new PropertiesReaderImpl();
    }

    public PropertiesWriter propertiesWriter() {
        return new PropertiesWriterImpl();
    }

    public String clientId() {
        return appConfig.getProperty("reddit.client_id");
    }

    public String clientSecret() {
        return appConfig.getProperty("reddit.client_secret");
    }

    public PersonalUseScript personalUseScript() {
        return new PersonalUseScript(clientId(), clientSecret());
    }

    public String appName() {
        return appConfig.getProperty("reddit.app_name");
    }

    public String version() {
        return appConfig.getProperty("reddit.version");
    }

    public String author() {
        return appConfig.getProperty("reddit.author");
    }

    public UserAgent userAgent() {
        return new UserAgent(appName(), version(), author());
    }

    public String username() {
        return appConfig.getProperty("reddit.username");
    }

    public String password() {
        return appConfig.getProperty("reddit.password");
    }

    public Credentials credentials() {
        return new Credentials(username(), password());
    }

    public RedditClientConfig redditClientConfig() {
        return new ScriptClientConfig(personalUseScript(), userAgent(), credentials());
    }

    public RedditClient redditClient() {
        return new RedditClient(redditClientConfig());
    }

    public long created() {
        return Long.parseLong(appConfig.getProperty("reddit.created", "0"));
    }
}
