package com.github.yvasyliev.service.deserializers.mappers;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.yvasyliev.model.dto.PostType;
import com.github.yvasyliev.model.dto.Post;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component
@Order(3)
public class PhotoGroupPostMapper implements PostMapper {
    @Value("10")
    private int pageSize;

    @Override
    public Optional<Post> applyWithException(JsonNode jsonPost) {
        if (jsonPost.has("gallery_data")) {
            var photoUrlsPages = extractPhotoUrlsPages(jsonPost);
            var title = jsonPost.get("title").textValue();
            var post = new Post();
            post.setType(PostType.PHOTO_GROUP);
            post.setText(title);
            post.setPhotoUrlsPages(photoUrlsPages);
            return Optional.of(post);
        }
        return Optional.empty();
    }

    private List<List<String>> extractPhotoUrlsPages(JsonNode post) {
        var photoUrls = extractPhotoUrls(post);
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

    private List<String> extractPhotoUrls(JsonNode post) {
        var items = post.get("gallery_data").get("items").elements();
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
