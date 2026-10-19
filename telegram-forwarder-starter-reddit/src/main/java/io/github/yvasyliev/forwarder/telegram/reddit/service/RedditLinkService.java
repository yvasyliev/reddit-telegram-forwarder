package io.github.yvasyliev.forwarder.telegram.reddit.service;

import io.github.yvasyliev.forwarder.telegram.reddit.configuration.RedditProperties;
import io.github.yvasyliev.forwarder.telegram.reddit.dto.Link;
import io.github.yvasyliev.forwarder.telegram.reddit.dto.Thing;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service for interacting with Reddit links.
 */
@Service
@RequiredArgsConstructor
public class RedditLinkService {
    private final RedditClient redditClient;
    private final RedditProperties redditProperties;

    /**
     * Fetches the new posts from the configured subreddit.
     *
     * @return a list of new Reddit links sorted by their natural order
     */
    public List<Link> getSubredditNew() {
        return redditClient.getSubredditNew(redditProperties.subreddit())
                .data()
                .children()
                .stream()
                .map(Thing::data)
                .sorted()
                .toList();
    }
}
