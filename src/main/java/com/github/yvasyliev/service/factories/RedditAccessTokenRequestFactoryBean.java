package com.github.yvasyliev.service.factories;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpRequest;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class RedditAccessTokenRequestFactoryBean implements FactoryBean<HttpRequest> {
    @Value("${REDDIT_CLIENT_ID}")
    private String redditClientId;

    @Value("${REDDIT_CLIENT_SECRET}")
    private String redditClientSecret;

    @Value("${REDDIT_USERNAME}")
    private String redditUsername;

    @Value("${REDDIT_PASSWORD}")
    private String redditPassword;

    @Autowired
    private String userAgent;

    @Override
    public HttpRequest getObject() {
        var credentials = "%s:%s".formatted(redditClientId, redditClientSecret);
        var authorization = "Basic %s".formatted(Base64.getEncoder().encodeToString(credentials.getBytes(StandardCharsets.UTF_8)));
        var payload = Map.of(
                "grant_type", "password",
                "username", redditUsername,
                "password", redditPassword
        );
        return HttpRequest.newBuilder(URI.create("https://www.reddit.com/api/v1/access_token"))
                .header("Authorization", authorization)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .header("User-Agent", userAgent)
                .POST(HttpRequest.BodyPublishers.ofString(encode(payload)))
                .build();
    }

    @Override
    public Class<?> getObjectType() {
        return HttpRequest.class;
    }

    private String encode(Map<String, String> payload) {
        return payload
                .entrySet()
                .stream()
                .map(this::encode)
                .collect(Collectors.joining("&"));
    }

    private String encode(Map.Entry<String, String> entry) {
        return "%s=%s".formatted(encode(entry.getKey()), encode(entry.getValue()));
    }

    private String encode(String s) {
        return URLEncoder.encode(s, StandardCharsets.UTF_8);
    }
}
