package com.github.yvasyliev.service.reddit.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.yvasyliev.dto.RedditAccessToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;
import java.util.stream.Collectors;

public class GetRedditAccessToken implements Request<RedditAccessToken> {
    @Value("${REDDIT_CLIENT_ID}")
    private String redditClientId;

    @Value("${REDDIT_CLIENT_SECRET}")
    private String redditClientSecret;

    @Value("${REDDIT_USERNAME}")
    private String redditUsername;

    @Value("${REDDIT_PASSWORD}")
    private String redditPassword;

    private HttpRequest request;

    @Autowired
    private HttpClient httpClient;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private String userAgent;

    public void initializeRequest() {
        var authorization = "Basic " + Base64.getEncoder().encodeToString((redditClientId + ":" + redditClientSecret).getBytes(StandardCharsets.UTF_8));
        var payload = Map.of(
                "grant_type", "password",
                "username", redditUsername,
                "password", redditPassword
        );
        request = HttpRequest.newBuilder(URI.create("https://www.reddit.com/api/v1/access_token"))
                .header("Authorization", authorization)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .header("User-Agent", userAgent)
                .POST(HttpRequest.BodyPublishers.ofString(encode(payload)))
                .build();
    }

    @Override
    public RedditAccessToken execute() throws IOException, InterruptedException {
        var jsonBody = httpClient.send(request, HttpResponse.BodyHandlers.ofString()).body();
        return objectMapper.readValue(jsonBody, RedditAccessToken.class);
    }

    private String encode(Map<String, String> payload) {
        return payload
                .entrySet()
                .stream()
                .map(this::encode)
                .collect(Collectors.joining("&"));
    }

    private String encode(Map.Entry<String, String> entry) {
        return encode(entry.getKey()) + "=" + encode(entry.getValue());
    }

    private String encode(String s) {
        return URLEncoder.encode(s, StandardCharsets.UTF_8);
    }
}
