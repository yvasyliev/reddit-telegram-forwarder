package com.github.yvasyliev.telegram.chain;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.yvasyliev.telegram.TelegramSenderBot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class RepeatMultiplePhotos extends SubredditPostRepeaterChain {
    private static final Logger LOGGER = LoggerFactory.getLogger(RepeatMultiplePhotos.class);

    @Value("10")
    private int pageSize;

    @Autowired
    @Qualifier("appData")
    public Properties appData;

    public RepeatMultiplePhotos(SubredditPostRepeaterChain nextChain) {
        super(nextChain);
    }

    @Override
    public void repeatRedditPost(JsonNode data, TelegramSenderBot telegramSenderBot, boolean needModerate) {
        if (data.has("gallery_data")) {
            var hasSpoiler = hasSpoiler(data);
            var photoUrlsPages = extractPhotoUrlsPages(data);
            try {
                for (var i = 0; i < photoUrlsPages.size(); i++) {
                    var photoUrls = photoUrlsPages.get(i);
                    var text = buildText(data.get("title").textValue(), i + 1, photoUrlsPages.size());

                    if (photoUrls.size() == 1) {
                        telegramSenderBot.sendPhoto(photoUrls.get(0), text, hasSpoiler, needModerate);
                    } else {
                        telegramSenderBot.sendMultiplePhotos(photoUrls, text, hasSpoiler, needModerate);
                        sleep(5);
                    }
                }
                appData.setProperty("PREVIOUS_REDDIT_POST_CREATED", String.valueOf(data.get("created").intValue()));
            } catch (TelegramApiException e) {
                LOGGER.error(
                        "Failed to send multiple Photos. Created: {}, URL: {}",
                        data.get("created").intValue(),
                        data.get("url_overridden_by_dest").textValue(),
                        e
                );
            }
        } else {
            super.repeatRedditPost(data, telegramSenderBot, needModerate);
        }
    }

    private List<List<String>> extractPhotoUrlsPages(JsonNode data) {
        var photoUrls = extractPhotoUrls(data);
        return IntStream
                .range(0, photoUrls.size())
                .boxed()
                .collect(Collectors.groupingBy(
                        i -> i / pageSize,
                        Collectors.mapping(photoUrls::get, Collectors.toList())
                ))
                .values()
                .stream()
                .toList();
    }

    private List<String> extractPhotoUrls(JsonNode data) {
        var items = data.get("gallery_data").get("items").elements();
        return stream(items)
                .map(item -> {
                    var mediaId = item.get("media_id").textValue();
                    var metadata = data.get("media_metadata").get(mediaId);
                    return stream(metadata.get("p").elements())
                            .max(Comparator.comparingInt(p -> p.get("x").intValue()));
                })
                .filter(Optional::isPresent)
                .map(optionalP -> optionalP.get().get("u").textValue())
                .toList();
    }

    private String buildText(String originalText, int pageNumber, int totalPages) {
        var text = originalText;

        if (totalPages > 1) {
            text = "%d/%d".formatted(pageNumber, totalPages);
            if (originalText != null && !originalText.isEmpty()) {
                text = "%s %s".formatted(originalText, text);
            }
        }

        return text;
    }

    private void sleep(long seconds) {
        try {
            TimeUnit.SECONDS.sleep(seconds);
        } catch (InterruptedException e) {
            LOGGER.error("Failed to sleep by {} seconds.", seconds, e);
        }
    }
}
