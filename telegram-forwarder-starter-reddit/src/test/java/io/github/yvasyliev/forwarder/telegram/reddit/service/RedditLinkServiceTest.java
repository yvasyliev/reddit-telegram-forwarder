package io.github.yvasyliev.forwarder.telegram.reddit.service;

import io.github.yvasyliev.forwarder.telegram.reddit.configuration.RedditProperties;
import io.github.yvasyliev.forwarder.telegram.reddit.dto.Link;
import io.github.yvasyliev.forwarder.telegram.reddit.dto.Listing;
import io.github.yvasyliev.forwarder.telegram.reddit.dto.Thing;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RedditLinkServiceTest {
    @InjectMocks private RedditLinkService redditLinkService;
    @Mock private RedditClient redditClient;
    @Mock private RedditProperties redditProperties;

    @Test
    void testGetSubredditNew() {
        var subreddit = "testSubreddit";
        var expected = mock(Link.class);
        var child = new Thing<>(null, expected);
        var subredditNew = new Thing<>(null, Listing.builder().child(child).build());

        when(redditProperties.subreddit()).thenReturn(subreddit);
        when(redditClient.getSubredditNew(subreddit)).thenReturn(subredditNew);

        var actual = redditLinkService.getSubredditNew();

        assertEquals(List.of(expected), actual);
    }
}
