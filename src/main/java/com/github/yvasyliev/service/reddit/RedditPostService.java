package com.github.yvasyliev.service.reddit;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.yvasyliev.service.reddit.api.Request;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class RedditPostService {
    @Autowired
    private Request<JsonNode> getSubredditNew;

    @Value("${PREVIOUS_REDDIT_POST_CREATED:0}")
    private int previousRedditPostCreated;

    @Value("30")
    private int delayMinutes;

    public List<JsonNode> findNewPosts() throws IOException, InterruptedException {
        var subredditPosts = getSubredditNew.execute();
        var children = subredditPosts
                .get("data")
                .get("children")
                .elements();
        var newPosts = StreamSupport
                .stream(Spliterators.spliteratorUnknownSize(children, Spliterator.ORDERED), false)
                .map(child -> child.get("data"))
                .filter(data -> {
                    var created = data.get("created").intValue();
                    var createdUtc = Instant.ofEpochMilli(created * 1000L);
                    var deadline = Instant.now().minus(delayMinutes, ChronoUnit.MINUTES);
                    return created > previousRedditPostCreated
                            && createdUtc.isBefore(deadline);
                })
                .collect(Collectors.toList());
        Collections.reverse(newPosts);
        return newPosts;
    }
}
