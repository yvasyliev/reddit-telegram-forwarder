package com.github.yvasyliev.service.deserializers.mappers;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.yvasyliev.model.dto.post.Post;
import com.github.yvasyliev.model.dto.post.VideoPost;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.function.ThrowingFunction;

import java.util.Optional;

@Component
@Order(5)
public class VideoPostMapper implements PostMapper {
    @Autowired
    @Qualifier("redditVideoDownloader")
    private ThrowingFunction<String, String> redditVideoDownloadUrlProvider;

    @Override
    public Optional<Post> applyWithException(JsonNode jsonPost) throws Exception {
        var videoUrl = extractVideoUrl(jsonPost);
        if (videoUrl != null) {
            var post = new VideoPost();
            post.setText(jsonPost.get("title").textValue());
            post.setMediaUrl(videoUrl);
            post.setHasSpoiler("nsfw".equals(jsonPost.get("thumbnail").textValue()));
            return Optional.of(post);
        }
        return Optional.empty();
    }

    private String extractVideoUrl(JsonNode data) throws Exception {
        if (data.get("is_video").booleanValue()) {
            var redditPostUrl = "https://www.reddit.com%s".formatted(data.get("permalink").textValue());
            return redditVideoDownloadUrlProvider.applyWithException(redditPostUrl);
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
