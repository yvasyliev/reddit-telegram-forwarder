package com.github.yvasyliev.service.reddit.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.yvasyliev.model.dto.RedditAccessToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
public class GetRedditAccessToken implements Request<RedditAccessToken> {
    @Autowired
    private HttpRequest redditAccessTokenRequest;

    @Autowired
    private HttpClient httpClient;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public RedditAccessToken execute() throws IOException, InterruptedException {
        var jsonBody = httpClient.send(redditAccessTokenRequest, HttpResponse.BodyHandlers.ofString()).body();
        return objectMapper.readValue(jsonBody, RedditAccessToken.class);
    }
}
