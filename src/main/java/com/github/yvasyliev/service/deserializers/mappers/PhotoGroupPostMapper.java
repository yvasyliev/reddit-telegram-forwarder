package com.github.yvasyliev.service.deserializers.mappers;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.yvasyliev.model.dto.post.PhotoGroupPost;
import com.github.yvasyliev.model.dto.post.Post;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.util.ArrayDeque;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component
@Order(3)
public class PhotoGroupPostMapper implements PostMapper {
    @Value("10")
    private int pageSize;

    @Override
    @NonNull
    public Optional<Post> applyWithException(JsonNode jsonPost) {
        if (jsonPost.has("gallery_data")) {
            var photoUrlsPages = extractPhotoUrlsPages(jsonPost);
            var title = jsonPost.get("title").textValue();
            var post = new PhotoGroupPost();
            post.setText(title);
            post.setPhotoUrlsPages(photoUrlsPages);
            post.setHasSpoiler("nsfw".equals(jsonPost.get("thumbnail").textValue()));
            return Optional.of(post);
        }
        return Optional.empty();
    }

    private Queue<Queue<String>> extractPhotoUrlsPages(JsonNode post) {
        var photoUrls = extractPhotoUrls(post);
        return new ArrayDeque<>(IntStream
                .range(0, photoUrls.size())
                .boxed()
                .collect(Collectors.groupingBy(
                        i -> i / pageSize,
                        Collectors.mapping(photoUrls::get, Collectors.toCollection(ArrayDeque::new))
                ))
                .values());
    }

    private List<String> extractPhotoUrls(JsonNode post) {
        var items = post.requiredAt("/gallery_data/items").elements();
        return stream(items)
                .map(item -> {
                    var mediaId = item.get("media_id").textValue();
                    var metadata = post.get("media_metadata").get(mediaId);
                    return stream(metadata.get("p").elements())
                            .max(Comparator.comparingInt(p -> p.get("x").intValue()));
                })
                .filter(Optional::isPresent)
                .map(optionalP -> optionalP.get().get("u").textValue())
                .toList();
    }
}
