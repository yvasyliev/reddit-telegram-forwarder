package com.github.yvasyliev.service.reddit;

import com.github.yvasyliev.exceptions.VideoUrlParseException;
import org.jsoup.Jsoup;

import java.io.IOException;

public class RedditVideoDownloader {
    public String getVideoDownloadUrl(String redditPostUrl) throws IOException, VideoUrlParseException {
        var url = "https://rapidsave.com/info?url=%s".formatted(redditPostUrl);
        var element = Jsoup.connect(url)
                .get()
                .select("div.download-info a")
                .first();
        if (element == null) {
            throw new VideoUrlParseException("Failed to parse video URL: %s".formatted(url));
        }
        return element.attr("href");
    }
}
