package app.funclub.anadearmas.services.impl;

import app.funclub.anadearmas.services.RedditPosts;
import com.github.masecla.RedditClient;
import com.github.masecla.objects.reddit.Link;
import com.github.masecla.objects.reddit.Thing;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class RedditPostsImpl implements RedditPosts {
    private final RedditClient redditClient;
    private final String subreddit;

    public RedditPostsImpl(RedditClient redditClient, String subreddit) {
        this.redditClient = redditClient;
        this.subreddit = subreddit;
    }

    @Override
    public List<Link> getNewPosts(long startTime) throws IOException {
        List<Link> newPosts = redditClient
                .getSubredditNew("AnadeArmas")
                .rawJson()
                .execute()
                .getData()
                .getChildren()
                .stream()
                .map(Thing::getData)
                .filter(post -> post.getCreated() > startTime)
                .collect(Collectors.toList());
        Collections.reverse(newPosts);
        return newPosts;
    }
}
