package com.github.yvasyliev.service.reddit;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.yvasyliev.model.dto.RedditAccessToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.function.ThrowingSupplier;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
public class RedditAccessTokenSupplier implements ThrowingSupplier<RedditAccessToken> {
    @Autowired
    private HttpRequest redditAccessTokenRequest;

    @Autowired
    private HttpClient httpClient;

    @Autowired
    private ObjectMapper objectMapper;

    private RedditAccessToken redditAccessToken;

    @Override
    public RedditAccessToken getWithException() throws IOException, InterruptedException {
        if (redditAccessToken == null || redditAccessToken.isExpired()) {
            var jsonBody = httpClient.send(redditAccessTokenRequest, HttpResponse.BodyHandlers.ofString()).body();
            redditAccessToken = objectMapper.readValue(jsonBody, RedditAccessToken.class);
        }
        return redditAccessToken;
    }
}
