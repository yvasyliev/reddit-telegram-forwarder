package com.github.yvasyliev.service.reddit.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.yvasyliev.dto.RedditAccessToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class GetSubredditNew implements Request<JsonNode> {
    @Autowired
    private ApplicationContext applicationContext;

    @Value("${SUBREDDIT}")
    private String subreddit;

    @Autowired
    private String userAgent;

    @Autowired
    private HttpClient httpClient;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public JsonNode execute() throws IOException, InterruptedException {
        var redditAccessToken = applicationContext.getBean(RedditAccessToken.class);
        var authorization = "Bearer " + redditAccessToken.token();
        var request = HttpRequest.newBuilder(URI.create("https://oauth.reddit.com/r/" + subreddit + "/new"))
                .header("Authorization", authorization)
                .header("User-Agent", userAgent)
                .GET()
                .build();
        var jsonBody = httpClient.send(request, HttpResponse.BodyHandlers.ofString()).body();
        return objectMapper.readTree(jsonBody);
    }
}
