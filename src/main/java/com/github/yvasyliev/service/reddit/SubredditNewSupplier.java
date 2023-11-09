package com.github.yvasyliev.service.reddit;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.yvasyliev.model.dto.RedditAccessToken;
import com.github.yvasyliev.model.dto.post.Post;
import com.github.yvasyliev.service.data.RedditTelegramForwarderPropertyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.util.function.ThrowingSupplier;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Objects;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.StreamSupport;

@Service
public class SubredditNewSupplier implements ThrowingSupplier<List<Post>> {
    private static final Logger LOGGER = LoggerFactory.getLogger(SubredditNewSupplier.class);

    @Autowired
    private RedditTelegramForwarderPropertyService propertyService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ThrowingSupplier<RedditAccessToken> redditAccessTokenSupplier;

    @Value("${reddit.subreddit}")
    private String subreddit;

    @Value("java:${project.artifactId}:${project.version} (by /u/${reddit.username})")
    private String userAgent;

    @Autowired
    private HttpClient httpClient;

    @Override
    @NonNull
    public List<Post> getWithException() throws Exception {
        var subredditPosts = fetchNewPosts();
        var children = subredditPosts.withArray("/data/children").elements();
        var lastCreated = propertyService.findLastCreated().orElse(0);
        return StreamSupport
                .stream(Spliterators.spliteratorUnknownSize(children, Spliterator.ORDERED), false)
                .map(child -> child.get("data"))
                .filter(data -> data.get("created").intValue() > lastCreated)
                .map(data -> {
                    try {
                        return objectMapper.treeToValue(data, Post.class);
                    } catch (JsonProcessingException e) {
                        LOGGER.error(
                                "Failed to deserialize post. Created: {}, URL: {}",
                                data.get("created").intValue(),
                                data.get("url").textValue(),
                                e
                        );
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .sorted()
                .toList();
    }

    private JsonNode fetchNewPosts() throws Exception {
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
