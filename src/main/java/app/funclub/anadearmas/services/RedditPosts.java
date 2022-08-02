package app.funclub.anadearmas.services;

import com.github.masecla.objects.reddit.Link;

import java.io.IOException;
import java.util.List;

@FunctionalInterface
public interface RedditPosts {
    List<Link> getNewPosts(long startTime) throws IOException;
}
