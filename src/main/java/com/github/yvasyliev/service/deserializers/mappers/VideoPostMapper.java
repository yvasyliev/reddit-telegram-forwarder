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
        return extractVideoUrl(jsonPost).map(videoUrl -> {
            var post = new VideoPost();
            post.setText(jsonPost.get("title").textValue());
            post.setMediaUrl(videoUrl);
            post.setHasSpoiler("nsfw".equals(jsonPost.get("thumbnail").textValue()));
            return post;
        });
    }

    private Optional<String> extractVideoUrl(JsonNode jsonPost) throws Exception {
        if (jsonPost.get("is_video").booleanValue()) {
            var redditPostUrl = "https://www.reddit.com%s".formatted(jsonPost.get("permalink").textValue());
            return redditVideoDownloadUrlProvider.applyWithException(redditPostUrl).describeConstable();
        }

        return Optional
                .ofNullable(jsonPost.at("/media/reddit_video/fallback_url").textValue())
                .or(() -> Optional.ofNullable(jsonPost.at("/preview/reddit_video_preview/fallback_url").textValue()));
    }
}
