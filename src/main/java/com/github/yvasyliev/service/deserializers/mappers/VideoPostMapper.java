package com.github.yvasyliev.service.deserializers.mappers;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.yvasyliev.model.dto.post.Post;
import com.github.yvasyliev.model.dto.post.VideoPost;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.function.ThrowingFunction;

@Component
@Order(5)
public class VideoPostMapper extends JsonNodeToPostConverter {
    @Autowired
    @Qualifier("redditVideoDownloader")
    private ThrowingFunction<String, String> redditVideoDownloadUrlProvider;

    @Override
    public Post convertThrowing(JsonNode jsonPost) throws Exception {
        var videoUrl = extractVideoUrl(jsonPost);

        if (videoUrl == null) {
            return convertNext(jsonPost);
        }

        var post = new VideoPost();
        post.setText(title(jsonPost));
        post.setMediaUrl(videoUrl);
        post.setHasSpoiler(nsfw(jsonPost));
        return post;
    }

    private String extractVideoUrl(JsonNode jsonPost) throws Exception {
        if (jsonPost.get("is_video").booleanValue()) {
            var redditPostUrl = "https://www.reddit.com%s".formatted(jsonPost.get("permalink").textValue());
            return redditVideoDownloadUrlProvider.applyWithException(redditPostUrl);
        }

        var videoUrl = jsonPost.at("/media/reddit_video/fallback_url").textValue();
        if (videoUrl == null) {
            videoUrl = jsonPost.at("/preview/reddit_video_preview/fallback_url").textValue();
        }
        return videoUrl;
    }
}
