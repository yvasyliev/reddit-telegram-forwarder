package com.github.yvasyliev.service.reddit;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.yvasyliev.model.dto.RedditAccessToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.util.function.ThrowingSupplier;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
public class SubredditNewSupplier implements ThrowingSupplier<JsonNode> {
    @Value("${SUBREDDIT}")
    private String subreddit;

    @Autowired
    private String userAgent;

    @Autowired
    private HttpClient httpClient;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ThrowingSupplier<RedditAccessToken> redditAccessTokenSupplier;

    @Override
    @NonNull
    public JsonNode getWithException() throws Exception {
        var redditAccessToken = redditAccessTokenSupplier.getWithException();
        var authorization = "Bearer %s".formatted(redditAccessToken.token());
        var api = "https://oauth.reddit.com/r/%s/new?raw_json=1".formatted(subreddit);
        var request = HttpRequest.newBuilder(URI.create(api))
                .header("Authorization", authorization)
                .header("User-Agent", userAgent)
                .GET()
                .build();
        var jsonBody = httpClient.send(request, HttpResponse.BodyHandlers.ofString()).body();
        return objectMapper.readTree(jsonBody);
    }
}
