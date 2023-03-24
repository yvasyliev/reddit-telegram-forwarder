package com.github.yvasyliev.telegram;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.yvasyliev.exceptions.VideoUrlParseException;
import com.github.yvasyliev.service.reddit.RedditVideoDownloader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.net.URL;
import java.util.Properties;

public class RepeatVideo extends SubredditPostRepeaterChain {
    private static final Logger LOGGER = LoggerFactory.getLogger(RepeatVideo.class);

    @Autowired
    private RedditVideoDownloader redditVideoDownloader;

    @Autowired
    @Qualifier("appData")
    public Properties appData;

    public RepeatVideo(SubredditPostRepeaterChain nextChain) {
        super(nextChain);
    }

    @Override
    public void repeatRedditPost(JsonNode data, TelegramSenderBot telegramSenderBot) {
        try {
            var videoUrl = extractVideoUrl(data);
            if (videoUrl != null) {
                var filename = videoUrl.substring(videoUrl.lastIndexOf('/') + 1);
                try (var inputStream = new URL(videoUrl).openStream()) {
                    telegramSenderBot.sendVideo(
                            inputStream,
                            filename,
                            data.get("title").textValue(),
                            hasSpoiler(data)
                    );
                }
                appData.setProperty("PREVIOUS_REDDIT_POST_CREATED", String.valueOf(data.get("created").intValue()));
            } else {
                super.repeatRedditPost(data, telegramSenderBot);
            }
        } catch (IOException | VideoUrlParseException | TelegramApiException e) {
            LOGGER.error(
                    "Failed to send video. Created: {}, URL: {}",
                    data.get("created").intValue(),
                    data.get("url_overridden_by_dest").textValue(),
                    e
            );
        }
    }

    private String extractVideoUrl(JsonNode data) throws IOException, VideoUrlParseException {
        if (data.get("is_video").booleanValue()) {
            var redditPostUrl = "https://www.reddit.com%s".formatted(data.get("permalink").textValue());
            return redditVideoDownloader.getVideoDownloadUrl(redditPostUrl);
        }

        if (data.has("media")) {
            var media = data.get("media");
            if (media.has("reddit_video")) {
                return media.get("reddit_video").get("fallback_url").textValue();
            }
        }

        if (data.has("preview")) {
            var preview = data.get("preview");
            if (preview.has("reddit_video_preview")) {
                return preview.get("reddit_video_preview").get("fallback_url").textValue();
            }
        }

        return null;
    }
}
