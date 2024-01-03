package com.github.yvasyliev.service.reddit;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.yvasyliev.model.dto.RedditAccessToken;
import org.apache.http.client.HttpResponseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.util.function.ThrowingSupplier;

import java.io.IOException;
import java.net.HttpURLConnection;
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
    @NonNull
    public RedditAccessToken getWithException() throws IOException, InterruptedException {
        if (redditAccessToken == null || redditAccessToken.isExpired()) {
            var response = httpClient.send(redditAccessTokenRequest, HttpResponse.BodyHandlers.ofString());
            var jsonBody = response.body();
            if (response.statusCode() >= HttpURLConnection.HTTP_BAD_REQUEST) {
                throw new HttpResponseException(response.statusCode(), jsonBody);
            }
            redditAccessToken = objectMapper.readValue(jsonBody, RedditAccessToken.class);
        }
        return redditAccessToken;
    }
}
