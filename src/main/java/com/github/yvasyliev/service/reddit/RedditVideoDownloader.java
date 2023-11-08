package com.github.yvasyliev.service.reddit;

import com.github.yvasyliev.exceptions.VideoUrlParseException;
import org.jsoup.Jsoup;
import org.springframework.stereotype.Service;
import org.springframework.util.function.ThrowingFunction;

import java.io.IOException;

// TODO: 11/7/2023 remove Jsoup
@Service
public class RedditVideoDownloader implements ThrowingFunction<String, String> {
    @Override
    public String applyWithException(String redditPostUrl) throws IOException {
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
