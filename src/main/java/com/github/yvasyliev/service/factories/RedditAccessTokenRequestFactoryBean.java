package com.github.yvasyliev.service.factories;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpRequest;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class RedditAccessTokenRequestFactoryBean implements FactoryBean<HttpRequest> {
    @Value("${reddit.client.id}")
    private String redditClientId;

    @Value("${reddit.client.secret}")
    private String redditClientSecret;

    @Value("${reddit.username}")
    private String redditUsername;

    @Value("${reddit.password}")
    private String redditPassword;

    @Value("java:${project.artifactId}:${project.version} (by /u/${reddit.username})")
    private String userAgent;

    @Value("#{T(java.time.Duration).ofMinutes(${http.request.timeout.in.minutes:2})}")
    private Duration timeout;

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
                .timeout(timeout)
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
