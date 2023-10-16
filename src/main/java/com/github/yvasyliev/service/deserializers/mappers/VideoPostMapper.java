package com.github.yvasyliev.service.deserializers.mappers;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.yvasyliev.model.dto.Post;
import com.github.yvasyliev.model.dto.PostType;
import com.github.yvasyliev.service.reddit.RedditVideoDownloader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Order(5)
public class VideoPostMapper implements PostMapper {
    @Autowired
    private RedditVideoDownloader redditVideoDownloader;

    @Override
    public Post apply(JsonNode jsonPost) throws IOException {
        var videoUrl = extractVideoUrl(jsonPost);
        if (videoUrl != null) {
            var post = new Post();
            post.setType(PostType.VIDEO);
            post.setText(jsonPost.get("title").textValue());
            post.setMediaUrl(videoUrl);
            return post;
        }
        return null;
    }

    private String extractVideoUrl(JsonNode data) throws IOException {
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
